/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ServerPreferences;
import org.eclipse.wst.server.core.util.ServerAdapter;
/**
 * A class that listens to the startup of a server. To use
 * it, just create an instance using one of the two constructors.
 *
 * This class will listen immediately for errors in the server
 * startup. However, it will not display an error until it has
 * been enabled. (setEnabled(true)) If you decide you want to
 * cancel the startup listener, just call setEnabled(false),
 * and the listener will be disposed of.
 */
public class ServerStartupListener {
	protected IServer server;
	protected Shell shell;
	protected IServerListener listener;

	protected IClient client;
	protected ILaunchableAdapter launchableAdapter;
	protected IModuleArtifact moduleObject;
	protected String launchMode;
	protected IModule module;

	// if true, ignores the first shutdown
	protected boolean ignoreShutdown = false;

	// if true, will output error messages
	protected boolean isEnabled = false;

	// if true, an error has occurred while this class was
	// disabled
	protected boolean isError = false;

	/**
	 * ServerStartupListener constructor comment.
	 */
	public ServerStartupListener(Shell shell, IServer server) {
		super();
		this.shell = shell;
		this.server = server;
	
		listener = new ServerAdapter() {
			public void serverStateChange(IServer server2) {
				handleStateChange(server2.getServerState());
			}
		};
		server.addServerListener(listener);
	}

	/**
	 * ServerStartupListener constructor comment.
	 */
	public ServerStartupListener(Shell shell, IServer server, IClient client, ILaunchableAdapter launchableAdapter, IModuleArtifact moduleObject, String launchMode, IModule module) {
		this(shell, server);
		this.client = client;
		this.launchableAdapter = launchableAdapter;
		this.launchMode = launchMode;
		this.module = module;
		this.moduleObject = moduleObject;
	}

	/**
	 * ServerStartupListener constructor comment.
	 */
	public ServerStartupListener(Shell shell, IServer server, IClient client, ILaunchableAdapter launchableAdapter, IModuleArtifact moduleObject, String launchMode, IModule module, boolean ignoreShutdown) {
		this(shell, server, client, launchableAdapter, moduleObject, launchMode, module);
		this.ignoreShutdown = ignoreShutdown;
	}

	/**
	 * ServerStartupListener constructor comment.
	 */
	public ServerStartupListener(Shell shell, IServer server, boolean ignoreShutdown) {
		this(shell, server);
		this.ignoreShutdown = ignoreShutdown;
	}
	
	/**
	 * Display the startup error notice.
	 */
	protected void displayError() {
		// display error notice
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String message = ServerUIPlugin.getResource("%errorServerStartFailed", server.getName());
				MessageDialog.openError(shell, ServerUIPlugin.getResource("%errorDialogTitle"), message);
			}
		});
	}

	/**
	 * Dispose of the listener and quit.
	 */
	protected void dispose() {
		if (listener != null)
			server.removeServerListener(listener);
		listener = null;
	}

	/**
	 * Handle a state change in the starting server.
	 *
	 * @param state byte
	 */
	protected void handleStateChange(int state) {
		switch (state) {
			case IServer.STATE_STARTED: {
				dispose();
				openClient();
				break;
			}
			case IServer.STATE_STOPPED:
			case IServer.STATE_STOPPING: {
				if (!ignoreShutdown) {
					dispose();
					if (isEnabled)
						displayError();
					else
						isError = true;
				} else if (ignoreShutdown && state == IServer.STATE_STOPPED) {
					ignoreShutdown = false;
				}
				break;
			}
		}
	}

	/**
	 * Open the client, if one exists.
	 */
	protected void openClient() {
		if (client == null)
			return;
		
		launchClientUtil(server, module, launchableAdapter, moduleObject, launchMode, client);
	}

	/**
	 * Allows the startup listener to be enabled or disabled.
	 *
	 * @param enabled boolean
	 */
	public void setEnabled(boolean enabled) {
		if (enabled) {
			if (isError)
				displayError();
			else
				isEnabled = true;
		} else
			dispose();
	}

	public static void launchClientUtil(final IServer server, final IModule module, final ILaunchableAdapter la, final IModuleArtifact mo, final String launchMode, final IClient client) {
		if (client == null || server == null)
			return;
	
		// initial implementation - should just wait for a module state change event
		if (server.getModuleState(module) == IServer.STATE_STARTING) {
			class DisplayClientJob extends Job {
				public DisplayClientJob() {
					super(ServerUIPlugin.getResource("%viewStatusStarting3"));
				}

				public IStatus run(IProgressMonitor monitor) {
					IStatus status = new Status(IStatus.OK, ServerUIPlugin.PLUGIN_ID, 0, "", null);

					// wait for up to 5 minutes
					int state = server.getModuleState(module);
					int count = ((ServerPreferences)ServerCore.getServerPreferences()).getModuleStartTimeout();
					while (state == IServer.STATE_STARTING && count > 0) {
						if (monitor.isCanceled())
							return status;
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
							// ignore
						}
						count -= 2000;
						state = server.getModuleState(module);
					}
					
					if (monitor.isCanceled())
						return status;
					
					if (state != IServer.STATE_STARTED)
						return status;
					
					// display client on UI thread
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							Trace.trace(Trace.FINEST, "Attempting to load client: " + client);
							try {
								ILaunchable launchable = la.getLaunchable(server, mo);
								client.launch(server, launchable, launchMode, server.getExistingLaunch());
							} catch (Exception e) {
								Trace.trace(Trace.SEVERE, "Server client failed", e);
							}
						}
					});
					return status;
				}
			}
			DisplayClientJob job = new DisplayClientJob();
			job.schedule();
			return;
		}
	
		// display client on UI thread
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Trace.trace(Trace.FINEST, "Attempting to load client: " + client);
				try {
					ILaunchable launchable = la.getLaunchable(server, mo);
					client.launch(server, launchable, launchMode, server.getExistingLaunch());
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Server client failed", e);
				}
			}
		});
	}
}