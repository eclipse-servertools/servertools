/**********************************************************************
 * Copyright (c) 2003, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
�* 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.actions;

import java.util.*;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.*;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.core.internal.ILaunchableAdapter;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.core.model.ModuleArtifactDelegate;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.internal.viewers.ModuleArtifactComposite;
import org.eclipse.wst.server.ui.internal.wizard.RunOnServerWizard;
/**
 * Support for starting/stopping server and clients for resources running on a server.
 */
public class RunOnServerActionDelegate implements IWorkbenchWindowActionDelegate {
	protected static final String[] launchModes = {
		ILaunchManager.RUN_MODE, ILaunchManager.DEBUG_MODE, ILaunchManager.PROFILE_MODE };

	public static String ROS_CLIENT = "ros_client";
	public static String ROS_LAUNCHABLE = "ros_launchable";
	
	protected Object selection;

	protected IWorkbenchWindow window;

	protected static Object globalSelection;

	protected static Map<String, Boolean> globalLaunchMode;
	protected String launchMode = ILaunchManager.RUN_MODE;

	protected boolean tasksAndClientShown;
	
	protected HashMap<String, Object> wiz_properties; 

	protected ILaunchableAdapter launchableAdapter;
	protected IClient client;

	/**
	 * RunOnServerActionDelegate constructor comment.
	 */
	public RunOnServerActionDelegate() {
		super();
	}
	
	/**
	 * RunOnServerActionDelegate constructor comment.
	 */
	public RunOnServerActionDelegate(HashMap<String, Object>properties) {
		this.wiz_properties = properties;
	}
	
	public void setActionProperties(HashMap<String,Object>properties){
		this.wiz_properties = properties;
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

	public IServer getServer(IModule module, IModuleArtifact moduleArtifact, IProgressMonitor monitor) throws CoreException {
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
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Could not add module to server", ce);
				}
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
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Launching wizard");
			}
			RunOnServerWizard wizard = new RunOnServerWizard(module, launchMode, moduleArtifact, wiz_properties);

			WizardDialog dialog = new WizardDialog(shell, wizard);
			if (dialog.open() == Window.CANCEL) {
				if (monitor != null)
					monitor.setCanceled(true);
				return null;
			}
			
			try {
				Job.getJobManager().join("org.eclipse.wst.server.ui.family", null);
			} catch (Exception e) {
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING, "Error waiting for job", e);
				}
			}
			server = wizard.getServer();
			boolean preferred = wizard.isPreferredServer();
			tasksAndClientShown = true;
			if (client == null || launchableAdapter == null){
				client = wizard.getSelectedClient();
				launchableAdapter = wizard.getLaunchableAdapter();
			}
			
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
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "Error waiting for job", e);
			}
		}
		
		return server;
	}

	/**
	 * Run the resource on a server.
	 */
	protected void run() {
		final IModuleArtifact[] moduleArtifacts = ServerPlugin.getModuleArtifacts(selection);
		if (moduleArtifacts == null || moduleArtifacts.length == 0 || moduleArtifacts[0] == null) {
			EclipseUtil.openError(Messages.errorNoArtifact);
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "No module artifact found");
			}
			return;
		}
		
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
		final IAdaptable info = new IAdaptable() {
			public Object getAdapter(Class adapter) {
				if (Shell.class.equals(adapter))
					return shell;
				if (String.class.equals(adapter))
					return "user";
 				return null;
			}
		};
		
		// If there is more than 1 moduleArtifact, get a valid ModuleArtifact that we can use for launching
		// TODO The ModuleArtifactComposite should be part of the RunOnServerWizard
		final IModuleArtifact moduleArtifact;
		if (moduleArtifacts.length > 1) {
			ModuleArtifactComposite artifactComposite = new ModuleArtifactComposite(shell, moduleArtifacts, launchMode);
			if (artifactComposite.open() == Window.CANCEL)
				return;
			
			moduleArtifact = artifactComposite.getSelection();
		} else
			moduleArtifact = moduleArtifacts[0];
		
		if (moduleArtifact.getModule() == null) { // 149425
			EclipseUtil.openError(Messages.errorNoModules);
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Module artifact not contained in a module");
			}
			return;
		}
		final IModule module = moduleArtifact.getModule();
		
		// check for servers with the given start mode
		IServer[] servers = ServerCore.getServers();
		boolean found = false;
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size && !found; i++) {
				if (ServerUIPlugin.isCompatibleWithLaunchMode(servers[i], launchMode)) {
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
					if (type.supportsLaunchMode(launchMode) && ServerUtil.isSupportedModule(moduleTypes, module.getModuleType())) {
						found = true;
					}
				}
			}
			if (!found) {
				EclipseUtil.openError(Messages.errorNoServer);
				if (Trace.FINEST) {
					Trace.trace(Trace.STRING_FINEST, "No server for start mode");
				}
				return;
			}
		}
		
		if (!ServerUIPlugin.saveEditors())
			return;

		tasksAndClientShown = false;
		IServer server2 = null;
		// initialize its value using the predefined value if one has been given
		client = (IClient)getOverwriteValue(ROS_CLIENT);
		launchableAdapter = (ILaunchableAdapter) getOverwriteValue(ROS_LAUNCHABLE);
		
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, 
					"Client and launchableAdapter after setting predefined values: launchableAdapter="
					+ launchableAdapter + " client=" + client);
		}		
		
		try {
			IProgressMonitor monitor = new NullProgressMonitor();
			server2 = getServer(module, moduleArtifact, monitor);
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
		
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Server: " + server);
		}
		
		if (server == null) {
			EclipseUtil.openError(Messages.errorNoServer);
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "No server found");
			}
			return;
		}
		
		if (!ServerUIPlugin.promptIfDirty(shell, server))
			return;
		
		// We need to check if the client and launchable were pre-populated
		if (!tasksAndClientShown) {
			RunOnServerWizard wizard = new RunOnServerWizard(server, launchMode, moduleArtifact, wiz_properties);
			if (wizard.shouldAppear()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				if (dialog.open() == Window.CANCEL)
					return;
			} else
				wizard.performFinish();

			// Do not overwrite the client or launchableAdapter value, as it may
			// have been set by getOverwriteValue, which will add predefined values 
			// if provided. There is no guarantee that getting the values (client and 
			// launchableAadapter) from the wizard will be valid, since the values from
			// the wizard are used only if the client and launchableAdapter are null
			if (client == null){
				client = wizard.getSelectedClient();
			}
			if (launchableAdapter == null){
				launchableAdapter = wizard.getLaunchableAdapter();
			}
		}
		
		// if there is no client, use a dummy
		if (client == null) {		
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
		
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, 
					"Prior to creating launch client jobs: launchableAdapter="+ launchableAdapter + " client=" + client);
		}
		
		if (moduleArtifact instanceof ModuleArtifactDelegate) {
			boolean canLoad = false;
			try {
				Class c = Class.forName(moduleArtifact.getClass().getName());
				if (c.newInstance() != null)
					canLoad = true;
			} catch (Throwable t) {
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING,
							"Could not load module artifact delegate class, switching to backup");
				}
			}
			if (canLoad) {
				try {
					IProgressMonitor monitor = new NullProgressMonitor();
					ILaunchConfiguration config = getLaunchConfiguration(server, (ModuleArtifactDelegate) moduleArtifact, launchableAdapter, client, monitor);
					config.launch(launchMode, monitor);
				} catch (CoreException ce) {
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Could not launch Run on Server", ce);
					}
				}
				return;
			}
		}
		
		Thread thread = new Thread("Run on Server") {
			public void run() {
				if (Trace.FINEST) {
					Trace.trace(Trace.STRING_FINEST, "Ready to launch");
				}
				
				// start server if it's not already started
				// and cue the client to start
				IModule[] modules = new IModule[] { module }; // TODO: get parent hierarchy correct
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
					
					final LaunchClientJob clientJob = new LaunchClientJob(server, modules, launchMode, moduleArtifact, launchableAdapter, client);
					if (restart) {
						final IServer server3 = server;
						server.restart(launchMode, new IServer.IOperationListener() {
							public void done(IStatus result) {
								// Only publish if the server requires publish before launching the client.
								if (server3.shouldPublish()) {
									server3.publish(IServer.PUBLISH_INCREMENTAL, null, info, new IServer.IOperationListener() {
										public void done(IStatus result2) {
											if (result2.isOK())
												clientJob.schedule();
										}
									});
								} else {
									clientJob.schedule();
								}
							}
						});
					} else {
						// Only publish if the server requires publish before launching the client.
						if (server.shouldPublish()) {
							server.publish(IServer.PUBLISH_INCREMENTAL, null, info, new IServer.IOperationListener() {
								public void done(IStatus result) {
									if (result.isOK())
										clientJob.schedule();
								}
							});
						} else {
							clientJob.schedule();
						}
					}
				} else if (state != IServer.STATE_STOPPING) {
					final LaunchClientJob clientJob = new LaunchClientJob(server, modules, launchMode, moduleArtifact, launchableAdapter, client);
					
					server.start(launchMode, new IServer.IOperationListener() {
						public void done(IStatus result) {
							if (result.isOK())
								clientJob.schedule();
						}
					});
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	protected void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy config, IServer server, ModuleArtifactDelegate moduleArtifact, ILaunchableAdapter launchableAdapter, IClient client) {
		String launchName = NLS.bind(Messages.runOnServerLaunchConfigName, moduleArtifact.getName());
		launchName = getValidLaunchConfigurationName(launchName);
		if (!launchName.equals(config.getName())) {
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			launchName = launchManager.generateUniqueLaunchConfigurationNameFrom(launchName);
			config.rename(launchName);
		}
		
		config.setAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_SERVER_ID, server.getId());
		config.setAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_MODULE_ARTIFACT, moduleArtifact.serialize());
		config.setAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_MODULE_ARTIFACT_CLASS, moduleArtifact.getClass().getName());
		if (launchableAdapter != null)
			config.setAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_LAUNCHABLE_ADAPTER_ID, launchableAdapter.getId());
		else
			config.setAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_LAUNCHABLE_ADAPTER_ID, (String)null);
		config.setAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_CLIENT_ID, client.getId());
		
		try {
			IProject project = moduleArtifact.getModule().getProject();
			config.setMappedResources(new IResource[] { project });
		} catch (Exception e) {
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "Could not associate launch with a project", e);
			}
		}
	}

	protected ILaunchConfiguration getLaunchConfiguration(IServer server, ModuleArtifactDelegate moduleArtifact, ILaunchableAdapter launchableAdapter2, IClient client2, IProgressMonitor monitor) throws CoreException {
		String serverId = server.getId();
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigType = launchManager.getLaunchConfigurationType("org.eclipse.wst.server.ui.launchConfigurationType");
		ILaunchConfiguration[] launchConfigs = null;
		try {
			launchConfigs = launchManager.getLaunchConfigurations(launchConfigType);
		} catch (CoreException e) {
			// ignore
		}
		
		if (launchConfigs != null) {
			int size = launchConfigs.length;
			for (int i = 0; i < size; i++) {
				List list = launchConfigs[i].getAttribute(IDebugUIConstants.ATTR_FAVORITE_GROUPS, (List)null);
				if (list == null || list.isEmpty()) {
					try {
						String serverId2 = launchConfigs[i].getAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_SERVER_ID, (String) null);
						if (serverId.equals(serverId2)) {
							final ILaunchConfigurationWorkingCopy wc = launchConfigs[i].getWorkingCopy();
							setupLaunchConfiguration(wc, server, moduleArtifact, launchableAdapter2, client2);
							if (wc.isDirty()) {
								try {
									return wc.doSave();
								} catch (CoreException ce) {
									if (Trace.SEVERE) {
										Trace.trace(Trace.STRING_SEVERE, "Error configuring launch", ce);
									}
								}
							}
							return launchConfigs[i];
						}
					} catch (CoreException e) {
						if (Trace.SEVERE) {
							Trace.trace(Trace.STRING_SEVERE, "Error configuring launch", e);
						}
					}
				}
			}
		}
		
		// create a new launch configuration
		String launchName = NLS.bind(Messages.runOnServerLaunchConfigName, moduleArtifact.getName());
		launchName = getValidLaunchConfigurationName(launchName);
		launchName = launchManager.generateUniqueLaunchConfigurationNameFrom(launchName); 
		ILaunchConfigurationWorkingCopy wc = launchConfigType.newInstance(null, launchName);
		wc.setAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_SERVER_ID, serverId);
		setupLaunchConfiguration(wc, server, moduleArtifact, launchableAdapter2, client2);
		return wc.doSave();
	}

	protected static final char[] INVALID_CHARS = new char[] {'/','\\', ':', '*', '?', '"', '<', '>', '|', '\0', '@', '&'};
	protected String getValidLaunchConfigurationName(String s) {
		if (s == null || s.length() == 0)
			return "1";
		int size = INVALID_CHARS.length;
		for (int i = 0; i < size; i++) {
			s = s.replace(INVALID_CHARS[i], '_');
		}
		return s;
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
	protected static int openOptionsDialog(final Shell shell, final String title, final String message, final boolean breakpointsOption) {
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
	protected static int openBreakpointDialog(final Shell shell) {
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
	protected static int openRestartDialog(final Shell shell) {
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
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Running on Server...");
		}
		try {
			run();
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Run on Server Error", e);
			}
		}
	}

	protected boolean isEnabled() {
		try {
			Boolean b = globalLaunchMode.get(getLaunchMode());
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
		return launchMode;
	}

	/**
	 * Set the launch mode.
	 * 
	 * @param launchMode a {@link ILaunchManager} launch mode
	 */
	public void setLaunchMode(String launchMode) {
		this.launchMode = launchMode;
	}

	/**
	 * Determine which clients can act on the current selection.
	 *
	 * @param action action proxy that handles presentation
	 *    portion of the plugin action
	 * @param sel current selection in the desktop
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "> selectionChanged");
		}
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
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Selection: " + selection);
			}
			if (selection != null)
				if (Trace.FINEST) {
					Trace.trace(Trace.STRING_FINEST, "Selection type: " + selection.getClass().getName());
				}
			globalSelection = selection;
			globalLaunchMode = new HashMap<String, Boolean>();
			if (!ServerPlugin.hasModuleArtifact(globalSelection)) {
				action.setEnabled(false);
				return;
			}
			
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "checking for module artifact");
			}
			// TODO - multiple module artifacts
			IModuleArtifact[] moduleArtifacts = ServerPlugin.getModuleArtifacts(globalSelection);
			IModuleArtifact moduleArtifact = null;
			if (moduleArtifacts != null && moduleArtifacts.length > 0)
				moduleArtifact = moduleArtifacts[0];
			
			IModule module = null;
			if (moduleArtifact != null)
				module = moduleArtifact.getModule();
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "moduleArtifact= " + moduleArtifact + ", module= " + module);
			}
			if (module != null)
				findGlobalLaunchModes(module);
			else {
				globalLaunchMode.put(ILaunchManager.RUN_MODE, new Boolean(true));
				globalLaunchMode.put(ILaunchManager.DEBUG_MODE, new Boolean(true));
				globalLaunchMode.put(ILaunchManager.PROFILE_MODE, new Boolean(true));
			}
		}
		
		action.setEnabled(isEnabled());
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "< selectionChanged " + (System.currentTimeMillis() - time));
		}
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
	
	protected void setMap(HashMap<String,Object> map){
		this.wiz_properties = map;
	}
	
	/**
	 * Returns the value from the hashmap provided in the constructor of this class
	 *  
	 * @param key  
	 * @param obj the type of object to be returned
	 * @return
	 */
	protected Object getOverwriteValue(String key){
		if (wiz_properties != null){ 
			Object o = wiz_properties.get(key);
			return o;	
		}
		return null;
	}
}