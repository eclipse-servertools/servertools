package org.eclipse.wst.server.ui.internal.actions;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.ui.ServerUIUtil;
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
	
	protected static boolean initialized = false;
	
	protected static Object globalSelection;

	protected static Map globalLaunchMode;

	private static transient List propertyListeners;

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
	 * @param window the window that provides the context for this delegate
	 */
	public void init(IWorkbenchWindow newWindow) {
		window = newWindow;
	}
	
	/**
	 * Add a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public static void addPropertyChangeListener(PropertyChangeListener listener) {
		if (propertyListeners == null)
			propertyListeners = new ArrayList();
		propertyListeners.add(listener);
	}
	
	/**
	 * Remove a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public static void removePropertyChangeListener(PropertyChangeListener listener) {
		if (propertyListeners != null)
			propertyListeners.remove(listener);
	}
	
	/**
	 * Fire a property change event.
	 */
	protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		if (propertyListeners == null)
			return;
	
		PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		try {
			Iterator iterator = propertyListeners.iterator();
			while (iterator.hasNext()) {
				try {
					PropertyChangeListener listener = (PropertyChangeListener) iterator.next();
					listener.propertyChange(event);
				} catch (Exception e) {
					Trace.trace("Error firing property change event", e);
				}
			}
		} catch (Exception e) {
			Trace.trace("Error in property event", e);
		}
	}

	/**
	 * Run the resource on a server.
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	protected void run(IProgressMonitor monitor) {
		String launchMode = getLaunchMode();
		firePropertyChangeEvent("launchMode", null, launchMode);

		IModuleObject[] moduleObjects = ServerUtil.getModuleObjects(selection);
		
		Shell shell;
		if (window != null)
			shell = window.getShell();
		else
			shell = ServerUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();

		if (moduleObjects == null || moduleObjects.length == 0) {
			EclipseUtil.openError(ServerUIPlugin.getResource("%errorNoModules"));
			Trace.trace(Trace.FINEST, "No modules");
			return;
		}
		IModule module = moduleObjects[0].getModule();
		if (moduleObjects.length > 1) {
			// check if the modules are all in the same module
			int size = moduleObjects.length;
			for (int i = 0; i < size; i++) {
				IModule module2 = moduleObjects[i].getModule();
				if (!module.equals(module2)) {
					EclipseUtil.openError("Too many module objects");
					Trace.trace(Trace.SEVERE, "Too many module objects! Unsupported!");
					return;
				}
			}
		}

		// check for servers with the given start mode
		IServer[] servers = ServerCore.getServers();
		boolean found = false;
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size && !found; i++) {
				if (ServerUtil.isCompatibleWithLaunchMode(servers[i], launchMode)) {
					try {
						IModule[] parents = servers[i].getParentModules(module, monitor);
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
					IModuleType mt = module.getModuleType();
					if (type.supportsLaunchMode(launchMode) && ServerUtil.isSupportedModule(moduleTypes, mt.getId(), mt.getVersion())) {
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
		
		if (!ServerUIUtil.saveEditors())
			return;

		IServer server = null;
		IProject project = module.getProject();
		if (project != null)
			server = ServerCore.getProjectProperties(project).getDefaultServer();
		
		
		// ignore preference if the server doesn't support this mode.
		if (server != null && !ServerUtil.isCompatibleWithLaunchMode(server, launchMode))
			server = null;

		boolean tasksRun = false;	
		if (server == null) {
			// try the full wizard
			SelectServerWizard wizard = new SelectServerWizard(module, launchMode);
			ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
			if (dialog.open() == Window.CANCEL) {
				return;
			}

			server = wizard.getServer();
			boolean preferred = wizard.isPreferredServer();
			tasksRun = true;

			// set preferred server if requested
			if (server != null && preferred && project != null) {
				try {
					ServerCore.getProjectProperties(project).setDefaultServer(server, new NullProgressMonitor());
				} catch (CoreException ce) {
					String message = ServerUIPlugin.getResource("%errorCouldNotSavePreference");
					ErrorDialog.openError(shell, ServerUIPlugin.getResource("%errorDialogTitle"), message, ce.getStatus());
				}
			}
		}
		Trace.trace(Trace.FINEST, "Server: " + server);
		
		if (server == null) {
			EclipseUtil.openError(ServerUIPlugin.getResource("%errorNoServer"));
			Trace.trace(Trace.SEVERE, "No server found");
			return;
		}

		if (!ServerUIUtil.promptIfDirty(shell, server))
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
		IModuleObject moduleObject = null;
		ILaunchable launchable = null;
		//IModuleObject[] mo = moduleObjects.iterator();
		if (moduleObjects != null) {
			int size = moduleObjects.length;
			for (int i = 0; i < size; i++) {
				IModuleObject moduleObject2 = moduleObjects[i];
				
				ILaunchableAdapter[] adapters = ServerCore.getLaunchableAdapters();
				if (adapters != null) {
					int size2 = adapters.length;
					for (int j = 0; j < size2 && moduleObject == null; j++) {
						ILaunchableAdapter adapter = adapters[j];
						try {
							ILaunchable launchable2 = adapter.getLaunchable(server, moduleObject2);
							Trace.trace(Trace.FINEST, "adapter= " + adapter + ", launchable= " + launchable2);
							if (launchable2 != null) {
								launchableAdapter = adapter;
								moduleObject = moduleObject2;
								launchable = launchable2;
							}
						} catch (Exception e) {
							Trace.trace(Trace.SEVERE, "Error in launchable adapter", e);
						}
					}
				}
			}
		}
		
		IClient[] clients = new IClient[0];
		if (launchable != null)
			clients = ServerUtil.getClients(server, launchable, launchMode);

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

		final IServerPreferences preferences = ServerCore.getServerPreferences();

		// start server if it's not already started
		// and cue the client to start
		int state = server.getServerState();
		if (state == IServer.STATE_STARTING) {
			ServerStartupListener listener = new ServerStartupListener(shell, server, client, launchableAdapter, moduleObject, launchMode, module);
			listener.setEnabled(true);
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
			if (restart) {
				server.restart(launchMode);
				
				if (preferences.isAutoPublishing() && !autoPublish(server))
					return;
				ServerStartupListener.launchClientUtil(server, module, launchableAdapter, moduleObject, launchMode, client);
			} else {
				if (preferences.isAutoPublishing() && !autoPublish(server))
					return;
	
				// open client
				ServerStartupListener.launchClientUtil(server, module, launchableAdapter, moduleObject, launchMode, client);
			}
		} else if (state != IServer.STATE_STOPPING) {
			ServerStartupListener listener = new ServerStartupListener(shell, server, client, launchableAdapter, moduleObject, launchMode, module);
			if (preferences.isAutoPublishing() && !autoPublish(server))
				return;

			try {
				EclipseUtil.startServer(shell, server, launchMode, listener);
			} catch (CoreException e) {
				// ignore
			}
		}
	}

	/**
	 * Automatically publish to the given server.
	 *
	 * @param server
	 * @return boolean - false if the current operation should be stopped
	 */
	protected boolean autoPublish(IServer server) {
		// publish first
		if (server.shouldPublish()) {
			IStatus publishStatus = ServerUIUtil.publishWithDialog(server, false);
	
			if (publishStatus == null || publishStatus.getSeverity() == IStatus.ERROR)
				return false;
		}
		return true;
	}

	/**
	 * Open a message dialog.
	 * 
	 * @param shell
	 * @param message
	 * @return
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
		if (!initialized) {
			initialized = true;
			
			IModuleFactory[] factories = ServerCore.getModuleFactories();
			if (factories != null) {
				int size = factories.length;
				for (int i = 0; i < size; i++)
					factories[i].getModules();
			}
			
			try {
				IModule module = ServerUtil.getModule(globalSelection, true);
				findGlobalLaunchModes(module);
				action.setEnabled(isEnabled());
			} catch (Exception e) {
				// ignore
			}
			if (!isEnabled()) {
				EclipseUtil.openError(ServerUIPlugin.getResource("%errorNoServer"));
				Trace.trace(Trace.FINEST, "Uninitialized");
				return;
			}
		}

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
	 * portion of the plugin action
	 * @param selection current selection in the desktop
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

		if (selection != globalSelection || !initialized) {
			Trace.trace(Trace.FINEST, "Selection: " + selection);
			if (selection != null)	
				Trace.trace(Trace.FINEST, "Selection type: " + selection.getClass().getName());
			globalSelection = selection;
			globalLaunchMode = new HashMap();
			try {
				Trace.trace(Trace.FINEST, "calling getModule() " + initialized);
				IModule module = ServerUtil.getModule(globalSelection, initialized);
				Trace.trace(Trace.FINEST, "module: " + module);
				findGlobalLaunchModes(module);
			} catch (Exception e) {
				Trace.trace(Trace.FINEST, "not initialized");
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
	protected boolean findGlobalLaunchModes(IModule module) {
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
		return false;
	}

	/**
	 * Returns true if the given server type can launch the module. 
	 */
	protected boolean isValidServerType(IServerType type, IModule module) {
		try {
			IRuntimeType runtimeType = type.getRuntimeType();
			IModuleType mt = module.getModuleType();
			ServerUtil.isSupportedModule(runtimeType.getModuleTypes(), mt.getId(), mt.getVersion());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	protected boolean supportsLaunchMode(IServer server, String launchMode) {
		return server.getServerType().supportsLaunchMode(launchMode);
	}
}