/**********************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.core.internal.ILaunchableAdapter;
import org.eclipse.wst.server.core.internal.PublishServerJob;
import org.eclipse.wst.server.core.internal.RestartServerJob;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.ServerType;
import org.eclipse.wst.server.core.internal.StartServerJob;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.internal.wizard.*;
import org.eclipse.swt.widgets.Display;
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

	protected boolean tasksAndClientShown;

	public ILaunchableAdapter launchableAdapter;
	public IClient client;

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

	public IServer getServer(IModule module, String launchMode, IModuleArtifact moduleArtifact, IProgressMonitor monitor) throws CoreException {
		IServer server = ServerCore.getDefaultServer(module);
		
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
			Trace.trace(Trace.FINEST, "Launching wizard");
			RunOnServerWizard wizard = new RunOnServerWizard(module, launchMode, moduleArtifact);
			ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
			if (dialog.open() == Window.CANCEL) {
				if (monitor != null)
					monitor.setCanceled(true);
				return null;
			}
			
			try {
				Job.getJobManager().join("org.eclipse.wst.server.ui.family", null);
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Error waiting for job", e);
			}
			server = wizard.getServer();
			boolean preferred = wizard.isPreferredServer();
			tasksAndClientShown = true;
			client = wizard.getSelectedClient();
			launchableAdapter = wizard.getLaunchableAdapter();
			
			// set preferred server if requested
			if (server != null && preferred) {
				try {
					ServerCore.setDefaultServer(module, server, monitor);
				} catch (CoreException ce) {
					String message = Messages.errorCouldNotSavePreference;
					ErrorDialog.openError(shell, Messages.errorDialogTitle, message, ce.getStatus());
				}
			}
		}
		
		try {
			Job.getJobManager().join("org.eclipse.wst.server.ui.family", new NullProgressMonitor());
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error waiting for job", e);
		}
		
		return server;
	}

	/**
	 * Run the resource on a server.
	 */
	protected void run() {
		final String launchMode2 = getLaunchMode();
		final IModuleArtifact moduleArtifact = ServerPlugin.loadModuleArtifact(selection);
		
		Shell shell2 = null;
		if (window != null)
			shell2 = window.getShell();
		else {
			try {
				shell2 = ServerUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
			} catch (Exception e) {
				// ignore
			}
			if (shell2 == null)
				shell2 = Display.getDefault().getActiveShell();
		}
		final Shell shell = shell2;
		
		if (moduleArtifact == null) {
			EclipseUtil.openError(Messages.errorNoArtifact);
			Trace.trace(Trace.FINEST, "No module artifact found");
			return;
		}
		if (moduleArtifact.getModule() == null) { // 149425
			EclipseUtil.openError(Messages.errorNoModules);
			Trace.trace(Trace.FINEST, "Module artifact not contained in a module");
			return;
		}
		final IModule module = moduleArtifact.getModule();
		
		// check for servers with the given start mode
		IServer[] servers = ServerCore.getServers();
		boolean found = false;
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size && !found; i++) {
				if (ServerUIPlugin.isCompatibleWithLaunchMode(servers[i], launchMode2)) {
					try {
						IModule[] parents = servers[i].getRootModules(module, null);
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
			if (serverTypes != null) {
				int size = serverTypes.length;
				for (int i = 0; i < size && !found; i++) {
					IServerType type = serverTypes[i];
					IModuleType[] moduleTypes = type.getRuntimeType().getModuleTypes();
					if (type.supportsLaunchMode(launchMode2) && ServerUtil.isSupportedModule(moduleTypes, module.getModuleType())) {
						found = true;
					}
				}
			}
			if (!found) {
				EclipseUtil.openError(Messages.errorNoServer);
				Trace.trace(Trace.FINEST, "No server for start mode");
				return;
			}
		}
		
		if (!ServerUIPlugin.saveEditors())
			return;
		
		tasksAndClientShown = false;
		IServer server2 = null;
		client = null;
		launchableAdapter = null;
		try {
			IProgressMonitor monitor = new NullProgressMonitor();
			server2 = getServer(module, launchMode2, moduleArtifact, monitor);
			if (monitor.isCanceled())
				return;
			
			if (server2 != null) {
				IFolder folder = server2.getServerConfiguration();
				if (folder != null && folder.getProject() != null && !folder.getProject().isOpen())
					folder.getProject().open(monitor);
			}
		} catch (CoreException ce) {
			EclipseUtil.openError(shell, ce.getLocalizedMessage());
			return;
		}
		final IServer server = server2;
		//if (monitor.isCanceled())
		//	return;
		
		Trace.trace(Trace.FINEST, "Server: " + server);
		
		if (server == null) {
			EclipseUtil.openError(Messages.errorNoServer);
			Trace.trace(Trace.SEVERE, "No server found");
			return;
		}
		
		if (!ServerUIPlugin.promptIfDirty(shell, server))
			return;
		
		if (!tasksAndClientShown) {
			RunOnServerWizard wizard = new RunOnServerWizard(server, launchMode2, moduleArtifact);
			if (wizard.shouldAppear()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() == Window.CANCEL)
					return;
			} else
				wizard.performFinish();
			client = wizard.getSelectedClient();
			launchableAdapter = wizard.getLaunchableAdapter();
		}
		
		Thread thread = new Thread("Run on Server") {
			public void run() {
				String launchMode = launchMode2;
				
				if (client == null) {
					// if there is no client, use a dummy
					client = new IClient() {
						public String getDescription() {
							return Messages.clientDefaultDescription;
						}

						public String getId() {
							return "org.eclipse.wst.server.ui.client.default";
						}

						public String getName() {
							return Messages.clientDefaultName;
						}

						public IStatus launch(IServer server3, Object launchable2, String launchMode3, ILaunch launch) {
							return Status.OK_STATUS;
						}

						public boolean supports(IServer server3, Object launchable2, String launchMode3) {
							return true;
						}
					};
				}
				
				Trace.trace(Trace.FINEST, "Ready to launch");
				
				// start server if it's not already started
				// and cue the client to start
				IModule[] modules = new IModule[] { module }; // TODO: get parent heirarchy correct
				int state = server.getServerState();
				if (state == IServer.STATE_STARTING) {
					LaunchClientJob clientJob = new LaunchClientJob(server, modules, launchMode, moduleArtifact, launchableAdapter, client);
					clientJob.schedule();
				} else if (state == IServer.STATE_STARTED) {
					boolean restart = false;
					String mode = server.getMode();
					IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
					boolean disabledBreakpoints = false;
					
					if (server.getServerRestartState()) {
						int result = openRestartDialog(shell);
						if (result == 0) {
							launchMode = mode;
							restart = true;
						} else if (result == 9) // cancel
							return;
					}
					if (!restart) {
						if (!ILaunchManager.RUN_MODE.equals(mode) && ILaunchManager.RUN_MODE.equals(launchMode)) {
							boolean breakpointsOption = false;
							if (breakpointManager.isEnabled() && ILaunchManager.DEBUG_MODE.equals(mode))
								breakpointsOption = true;
							int result = openOptionsDialog(shell, Messages.wizRunOnServerTitle, Messages.dialogModeWarningRun, breakpointsOption);
							if (result == 0)
								restart = true;
							else if (result == 1) {
								breakpointManager.setEnabled(false);
								disabledBreakpoints = true;
								launchMode = mode;
							} else if (result == 2)
								launchMode = mode;
							else // result == 9 // cancel
								return;
						} else if (!ILaunchManager.DEBUG_MODE.equals(mode) && ILaunchManager.DEBUG_MODE.equals(launchMode)) {
							int result = openOptionsDialog(shell, Messages.wizDebugOnServerTitle, Messages.dialogModeWarningDebug, false);
							if (result == 0)
								restart = true;
							else if (result == 1)
								launchMode = mode;
							else // result == 9 // cancel
								return;
						} else if (!ILaunchManager.PROFILE_MODE.equals(mode) && ILaunchManager.PROFILE_MODE.equals(launchMode)) {
							boolean breakpointsOption = false;
							if (breakpointManager.isEnabled() && ILaunchManager.DEBUG_MODE.equals(mode))
								breakpointsOption = true;
							int result = openOptionsDialog(shell, Messages.wizProfileOnServerTitle, Messages.dialogModeWarningProfile, breakpointsOption);
							if (result == 0)
								restart = true;
							else if (result == 1) {
								breakpointManager.setEnabled(false);
								disabledBreakpoints = true;
								launchMode = mode;
							} else if (result == 2)
								launchMode = mode;
							else // result == 9 // cancel
								return;
						}
						
						if (ILaunchManager.DEBUG_MODE.equals(launchMode)) {
							if (!breakpointManager.isEnabled() && !disabledBreakpoints) {
								int result = openBreakpointDialog(shell);
								if (result == 0)
									breakpointManager.setEnabled(true);
								else if (result == 1) {
									// ignore
								} else // result == 2
									return;
							}
						}
					}
					
					PublishServerJob publishJob = new PublishServerJob(server, IServer.PUBLISH_INCREMENTAL, false);
					LaunchClientJob clientJob = new LaunchClientJob(server, modules, launchMode, moduleArtifact, launchableAdapter, client);
					publishJob.setNextJob(clientJob);
					
					if (restart) {
						RestartServerJob restartJob = new RestartServerJob(server, launchMode);
						restartJob.setNextJob(publishJob);
						restartJob.schedule();
					} else
						publishJob.schedule();
				} else if (state != IServer.STATE_STOPPING) {
					PublishServerJob publishJob = new PublishServerJob(server);
					StartServerJob startServerJob = new StartServerJob(server, launchMode);
					LaunchClientJob clientJob = new LaunchClientJob(server, modules, launchMode, moduleArtifact, launchableAdapter, client);
					
					if (((ServerType)server.getServerType()).startBeforePublish()) {
						startServerJob.setNextJob(publishJob);
						publishJob.setNextJob(clientJob);
						startServerJob.schedule();
					} else {
						publishJob.setNextJob(startServerJob);
						startServerJob.setNextJob(clientJob);
						publishJob.schedule();
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Open an options dialog.
	 * 
	 * @param shell
	 * @param title
	 * @param message
	 * @param breakpointsOption
	 * @return a dialog return constant
	 */
	protected int openOptionsDialog(final Shell shell, final String title, final String message, final boolean breakpointsOption) {
		if (breakpointsOption) {
			int current = ServerUIPlugin.getPreferences().getLaunchMode2();
			if (current == ServerUIPreferences.LAUNCH_MODE2_RESTART)
				return 0;
			else if (current == ServerUIPreferences.LAUNCH_MODE2_DISABLE_BREAKPOINTS)
				return 1;
			else if (current == ServerUIPreferences.LAUNCH_MODE2_CONTINUE)
				return 2;
		} else {
			int current = ServerUIPlugin.getPreferences().getLaunchMode();
			if (current == ServerUIPreferences.LAUNCH_MODE_RESTART)
				return 0;
			else if (current == ServerUIPreferences.LAUNCH_MODE_CONTINUE)
				return 1;
		}
		final int[] i = new int[1];
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				OptionsMessageDialog dialog = null;
				String[] items = null;
				if (breakpointsOption) {
					items = new String[] {
						Messages.dialogModeWarningRestart,
						Messages.dialogModeWarningBreakpoints,
						Messages.dialogModeWarningContinue
					};
				} else {
					items = new String[] {
						Messages.dialogModeWarningRestart,
						Messages.dialogModeWarningContinue
					};
				}
				
				dialog = new OptionsMessageDialog(shell, title, message, items);
				i[0] = dialog.open();
				
				if (dialog.isRemember()) {
					if (breakpointsOption) {
						if (i[0] == 0)
							ServerUIPlugin.getPreferences().setLaunchMode2(ServerUIPreferences.LAUNCH_MODE2_RESTART);
						else if (i[0] == 1)
							ServerUIPlugin.getPreferences().setLaunchMode2(ServerUIPreferences.LAUNCH_MODE2_DISABLE_BREAKPOINTS);
						else if (i[0] == 2)
							ServerUIPlugin.getPreferences().setLaunchMode2(ServerUIPreferences.LAUNCH_MODE2_CONTINUE);
					} else {
						if (i[0] == 0)
							ServerUIPlugin.getPreferences().setLaunchMode(ServerUIPreferences.LAUNCH_MODE_RESTART);
						else if (i[0] == 1)
							ServerUIPlugin.getPreferences().setLaunchMode(ServerUIPreferences.LAUNCH_MODE_CONTINUE);
					}
				}
			}
		});
		return i[0];
	}

	/**
	 * Open an options dialog.
	 * 
	 * @param shell
	 * @return a dialog return constant
	 */
	protected int openBreakpointDialog(final Shell shell) {
		int current = ServerUIPlugin.getPreferences().getEnableBreakpoints();
		if (current == ServerUIPreferences.ENABLE_BREAKPOINTS_ALWAYS)
			return 0;
		else if (current == ServerUIPreferences.ENABLE_BREAKPOINTS_NEVER)
			return 1;
		
		final int[] i = new int[1];
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				OptionsMessageDialog dialog = new OptionsMessageDialog(shell,
						Messages.wizDebugOnServerTitle, Messages.dialogBreakpoints, new String[] {
						Messages.dialogBreakpointsReenable, Messages.dialogModeWarningContinue});
				i[0] = dialog.open();
				if (dialog.isRemember()) {
					if (i[0] == 0)
						ServerUIPlugin.getPreferences().setEnableBreakpoints(ServerUIPreferences.ENABLE_BREAKPOINTS_ALWAYS);
					else if (i[0] == 1)
						ServerUIPlugin.getPreferences().setEnableBreakpoints(ServerUIPreferences.ENABLE_BREAKPOINTS_NEVER);
				}
			}
		});
		return i[0];
	}

	/**
	 * Open a restart options dialog.
	 * 
	 * @param shell
	 * @return a dialog return constant
	 */
	protected int openRestartDialog(final Shell shell) {
		int current = ServerUIPlugin.getPreferences().getRestart();
		if (current == ServerUIPreferences.RESTART_ALWAYS)
			return 0;
		else if (current == ServerUIPreferences.RESTART_NEVER)
			return 1;
		
		final int[] i = new int[1];
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				OptionsMessageDialog dialog = new OptionsMessageDialog(shell,
						Messages.defaultDialogTitle, Messages.dialogRestart, new String[] {
						Messages.dialogRestartRestart, Messages.dialogRestartContinue});
				i[0] = dialog.open();
				if (dialog.isRemember()) {
					if (i[0] == 0)
						ServerUIPlugin.getPreferences().setRestart(ServerUIPreferences.RESTART_ALWAYS);
					else if (i[0] == 1)
						ServerUIPlugin.getPreferences().setRestart(ServerUIPreferences.RESTART_NEVER);
				}
			}
		});
		return i[0];
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
			run();
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
		if (iterator.hasNext())
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