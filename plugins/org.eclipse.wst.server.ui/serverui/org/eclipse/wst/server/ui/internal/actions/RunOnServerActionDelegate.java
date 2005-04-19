/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.core.internal.ILaunchableAdapter;
import org.eclipse.wst.server.core.internal.PublishServerJob;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.internal.wizard.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
/**
 * Support for starting/stopping server and clients for resources running on a server.
 */
public class RunOnServerActionDelegate implements IWorkbenchWindowActionDelegate {
	protected static final String[] launchModes = {
		ILaunchManager.RUN_MODE, ILaunchManager.DEBUG_MODE, ILaunchManager.PROFILE_MODE };

	protected Object selection;

	protected IWorkbenchWindow window;

	protected static Object globalSelection;

	protected static Map globalLaunchMode;
	
	protected boolean tasksRun;

	/**
	 * RunOnServerActionDelegate constructor comment.
	 */
	public RunOnServerActionDelegate() {
		super();
	}

	/**
	 * Disposes this action delegate.  The implementor should unhook any references
	 * to itself so that garbage collection can occur.
	 */
	public void dispose() {
		window = null;
	}

	/**
	 * Initializes this action delegate with the workbench window it will work in.
	 *
	 * @param newWindow the window that provides the context for this delegate
	 */
	public void init(IWorkbenchWindow newWindow) {
		window = newWindow;
	}
	
	public IServer getServer(IModule module, String launchMode, IProgressMonitor monitor) {
		IServer server = null;
		IProject project = module.getProject();
		if (project != null)
			server = ServerCore.getProjectProperties(project).getDefaultServer();
		
		// ignore preference if the server doesn't support this mode.
		if (server != null && !ServerUIPlugin.isCompatibleWithLaunchMode(server, launchMode))
			server = null;
		
		if (server != null && !ServerUtil.containsModule(server, module, monitor)) {
			IServerWorkingCopy wc = server.createWorkingCopy();
			try {
				ServerUtil.modifyModules(wc, new IModule[] { module }, new IModule[0], monitor);
				wc.save(false, monitor);
			} catch (CoreException ce) {
				Trace.trace(Trace.SEVERE, "Could not add module to server", ce);
				server = null;
			}
		}
		
		Shell shell;
		if (window != null)
			shell = window.getShell();
		else
			shell = ServerUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();

		if (server == null) {
			// try the full wizard
			RunOnServerWizard wizard = new RunOnServerWizard(module, launchMode);
			ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
			if (dialog.open() == Window.CANCEL) {
				monitor.setCanceled(true);
				return null;
			}

			try {
				Platform.getJobManager().join("org.eclipse.wst.server.ui.family", new NullProgressMonitor());
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Error waiting for job", e);
			}
			server = wizard.getServer();
			boolean preferred = wizard.isPreferredServer();
			tasksRun = true;

			// set preferred server if requested
			if (server != null && preferred && project != null) {
				try {
					ServerCore.getProjectProperties(project).setDefaultServer(server, monitor);
				} catch (CoreException ce) {
					String message = ServerUIPlugin.getResource("%errorCouldNotSavePreference");
					ErrorDialog.openError(shell, ServerUIPlugin.getResource("%errorDialogTitle"), message, ce.getStatus());
				}
			}
		}
		
		try {
			Platform.getJobManager().join("org.eclipse.wst.server.ui.family", new NullProgressMonitor());
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error waiting for job", e);
		}
		
		return server;
	}

	/**
	 * Run the resource on a server.
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	protected void run(IProgressMonitor monitor) {
		String launchMode = getLaunchMode();
		IModuleArtifact moduleArtifact = ServerPlugin.loadModuleArtifact(selection);
		
		Shell shell;
		if (window != null)
			shell = window.getShell();
		else
			shell = ServerUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();

		if (moduleArtifact == null || moduleArtifact.getModule() == null) {
			EclipseUtil.openError(ServerUIPlugin.getResource("%errorNoModules"));
			Trace.trace(Trace.FINEST, "No modules");
			return;
		}
		IModule module = moduleArtifact.getModule();

		// check for servers with the given start mode
		IServer[] servers = ServerCore.getServers();
		boolean found = false;
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size && !found; i++) {
				if (ServerUIPlugin.isCompatibleWithLaunchMode(servers[i], launchMode)) {
					try {
						IModule[] parents = servers[i].getRootModules(module, monitor);
						if (parents != null && parents.length > 0)
							found = true;
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}

		if (!found) {
			// no existing server supports the project and start mode!
			// check if there might be another one that can be created
			IServerType[] serverTypes = ServerCore.getServerTypes();
			boolean found2 = false;
			if (serverTypes != null) {
				int size = serverTypes.length;
				for (int i = 0; i < size && !found2; i++) {
					IServerType type = serverTypes[i];
					IModuleType[] moduleTypes = type.getRuntimeType().getModuleTypes();
					if (type.supportsLaunchMode(launchMode) && ServerUtil.isSupportedModule(moduleTypes, module.getModuleType())) {
						found2 = true;
					}
				}
			}
			if (!found2) {
				EclipseUtil.openError(ServerUIPlugin.getResource("%errorNoServer"));
				Trace.trace(Trace.FINEST, "No server for start mode");
				return;
			}
		}
		
		if (!ServerUIPlugin.saveEditors())
			return;

		tasksRun = false;
		IServer server = getServer(module, launchMode, monitor);
		if (monitor.isCanceled())
			return;
		
		Trace.trace(Trace.FINEST, "Server: " + server);
		
		if (server == null) {
			EclipseUtil.openError(ServerUIPlugin.getResource("%errorNoServer"));
			Trace.trace(Trace.SEVERE, "No server found");
			return;
		}

		if (!ServerUIPlugin.promptIfDirty(shell, server))
			return;
		
		if (!tasksRun) {
			SelectTasksWizard wizard = new SelectTasksWizard(server);
			wizard.addPages();
			if (wizard.hasTasks() && wizard.hasOptionalTasks()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() == Window.CANCEL)
					return;
			} else
				wizard.performFinish();
		}
		
		// get the launchable adapter and module object
		ILaunchableAdapter launchableAdapter = null;
		Object launchable = null;
		ILaunchableAdapter[] adapters = ServerPlugin.getLaunchableAdapters();
		if (adapters != null) {
			int size2 = adapters.length;
			for (int j = 0; j < size2; j++) {
				ILaunchableAdapter adapter = adapters[j];
				try {
					Object launchable2 = adapter.getLaunchable(server, moduleArtifact);
					Trace.trace(Trace.FINEST, "adapter= " + adapter + ", launchable= " + launchable2);
					if (launchable2 != null) {
						launchableAdapter = adapter;
						launchable = launchable2;
					}
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error in launchable adapter", e);
				}
			}
		}
		
		IClient[] clients = new IClient[0];
		if (launchable != null)
			clients = getClients(server, launchable, launchMode);

		Trace.trace(Trace.FINEST, "Launchable clients: " + clients);

		IClient client = null;
		if (clients == null || clients.length == 0) {
			EclipseUtil.openError(ServerUIPlugin.getResource("%errorNoClient"));
			Trace.trace(Trace.SEVERE, "No launchable clients!");
			return;
		} else if (clients.length == 1) {
			client = clients[0];
		} else {
			SelectClientWizard wizard = new SelectClientWizard(clients);
			ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
			dialog.open();
			client = wizard.getSelectedClient();
			if (client == null)
				return;
		}

		Trace.trace(Trace.FINEST, "Ready to launch");

		// start server if it's not already started
		// and cue the client to start
		IModule[] modules = new IModule[] { module }; // TODO: get parent heirarchy correct
		int state = server.getServerState();
		if (state == IServer.STATE_STARTING) {
			LaunchClientJob.launchClient(server, modules, launchMode, moduleArtifact, launchableAdapter, client);
		} else if (state == IServer.STATE_STARTED) {
			boolean restart = false;
			String mode = server.getMode();
			if (!ILaunchManager.DEBUG_MODE.equals(mode) && ILaunchManager.DEBUG_MODE.equals(launchMode)) {
				int result = openWarningDialog(shell, ServerUIPlugin.getResource("%dialogModeWarningDebug"));
				if (result == 1)
					launchMode = mode;
				else if (result == 0)
					restart = true;
				else
					return;
			} else if (!ILaunchManager.PROFILE_MODE.equals(mode) && ILaunchManager.PROFILE_MODE.equals(launchMode)) {
				int result = openWarningDialog(shell, ServerUIPlugin.getResource("%dialogModeWarningProfile"));
				if (result == 1)
					launchMode = mode;
				else if (result == 0)
					restart = true;
				else
					return;
			}
			if (restart)
				RestartServerJob.restartServer(server, launchMode);
			
			PublishServerJob publishJob = new PublishServerJob(server);
			publishJob.schedule();
			LaunchClientJob.launchClient(server, modules, launchMode, moduleArtifact, launchableAdapter, client);
		} else if (state != IServer.STATE_STOPPING) {
			PublishServerJob publishJob = new PublishServerJob(server);
			publishJob.schedule();
			StartServerJob.startServer(server, launchMode);
			LaunchClientJob.launchClient(server, modules, launchMode, moduleArtifact, launchableAdapter, client);
		}
	}

	/**
	 * Returns the launchable clients for the given server and launchable
	 * object.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 * @param launchable
	 * @param launchMode String
	 * @return an array of clients
	 */
	public static IClient[] getClients(IServer server, Object launchable, String launchMode) {
		ArrayList list = new ArrayList();
		IClient[] clients = ServerPlugin.getClients();
		if (clients != null) {
			int size = clients.length;
			for (int i = 0; i < size; i++) {
				Trace.trace(Trace.FINEST, "client= " + clients[i]);
				if (clients[i].supports(server, launchable, launchMode))
					list.add(clients[i]);
			}
		}
		
		IClient[] clients2 = new IClient[list.size()];
		list.toArray(clients2);
		return clients2;
	}

	/**
	 * Open a message dialog.
	 * 
	 * @param shell
	 * @param message
	 * @return a dialog return constant
	 */
	protected int openWarningDialog(Shell shell, String message) {
		MessageDialog dialog = new MessageDialog(shell, ServerUIPlugin.getResource("%errorDialogTitle"), null,
			message,	MessageDialog.WARNING, new String[] {ServerUIPlugin.getResource("%dialogModeWarningRestart"),
			ServerUIPlugin.getResource("%dialogModeWarningContinue"), IDialogConstants.CANCEL_LABEL}, 0);
		return dialog.open();
	}

	/**
	 * The delegating action has been performed. Implement
	 * this method to do the actual work.
	 *
	 * @param action action proxy that handles the presentation
	 * portion of the plugin action
	 */
	public void run(IAction action) {
		Trace.trace(Trace.FINEST, "Running on Server...");
		try {
			run(new NullProgressMonitor());
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Run on Server Error", e);
		}
	}
	
	protected boolean isEnabled() {
		try {
			Boolean b = (Boolean) globalLaunchMode.get(getLaunchMode());
			return b.booleanValue();
		} catch (Exception e) {
			// ignore
		}
		return false;
	}

	/**
	 * Returns the start mode that the server should use.
	 */
	protected String getLaunchMode() {
		return ILaunchManager.RUN_MODE;
	}

	/**
	 * Determine which clients can act on the current selection.
	 *
	 * @param action action proxy that handles presentation
	 *    portion of the plugin action
	 * @param sel current selection in the desktop
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		Trace.trace(Trace.FINEST, "> selectionChanged");
		selection = null;
		long time = System.currentTimeMillis();
		if (sel == null || sel.isEmpty() || !(sel instanceof IStructuredSelection)) {
			action.setEnabled(false);
			globalSelection = null;
			return;
		}

		IStructuredSelection select = (IStructuredSelection) sel;
		Iterator iterator = select.iterator();
		selection = iterator.next();
		if (iterator.hasNext()) { // more than one selection (should never happen)
			action.setEnabled(false);
			selection = null;
			globalSelection = null;
			return;
		}

		if (selection != globalSelection) {
			Trace.trace(Trace.FINEST, "Selection: " + selection);
			if (selection != null)	
				Trace.trace(Trace.FINEST, "Selection type: " + selection.getClass().getName());
			globalSelection = selection;
			globalLaunchMode = new HashMap();
			if (!ServerPlugin.hasModuleArtifact(globalSelection)) {
				action.setEnabled(false);
				return;
			}
			
			Trace.trace(Trace.FINEST, "checking for module artifact");
			IModuleArtifact moduleArtifact = ServerPlugin.getModuleArtifact(globalSelection);
			IModule module = null;
			if (moduleArtifact != null)
				module = moduleArtifact.getModule();
			Trace.trace(Trace.FINEST, "moduleArtifact= " + moduleArtifact + ", module= " + module);
			if (module != null)
				findGlobalLaunchModes(module);
			else {
				globalLaunchMode.put(ILaunchManager.RUN_MODE, new Boolean(true));
				globalLaunchMode.put(ILaunchManager.DEBUG_MODE, new Boolean(true));
				globalLaunchMode.put(ILaunchManager.PROFILE_MODE, new Boolean(true));
			}
		}

		action.setEnabled(isEnabled());
		Trace.trace(Trace.FINEST, "< selectionChanged " + (System.currentTimeMillis() - time));
	}
	
	/**
	 * Determines whether there is a server factory available for the given module
	 * and the various start modes.
	 */
	protected void findGlobalLaunchModes(IModule module) {
		IServerType[] serverTypes = ServerCore.getServerTypes();
		if (serverTypes != null) {
			int size = serverTypes.length;
			for (int i = 0; i < size; i++) {
				IServerType type = serverTypes[i];
				if (isValidServerType(type, module)) {
					for (byte b = 0; b < launchModes.length; b++) {
						if (type.supportsLaunchMode(launchModes[b])) {
							globalLaunchMode.put(launchModes[b], new Boolean(true));
						}
					}
				}
			}
		}
	}

	/**
	 * Returns true if the given server type can launch the module. 
	 */
	protected boolean isValidServerType(IServerType type, IModule module) {
		try {
			IRuntimeType runtimeType = type.getRuntimeType();
			ServerUtil.isSupportedModule(runtimeType.getModuleTypes(), module.getModuleType());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	protected boolean supportsLaunchMode(IServer server, String launchMode) {
		return server.getServerType().supportsLaunchMode(launchMode);
	}
}