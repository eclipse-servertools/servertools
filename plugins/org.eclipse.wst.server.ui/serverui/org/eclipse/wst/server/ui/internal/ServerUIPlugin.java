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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.util.ServerAdapter;
import org.eclipse.wst.server.ui.IServerUIPreferences;
import org.eclipse.wst.server.ui.ServerUICore;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
/**
 * The server UI plugin class.
 */
public class ServerUIPlugin extends AbstractUIPlugin {
	public static final byte START = 0;
	public static final byte STOP = 1;
	public static final byte RESTART = 2;
	
	// singleton instance of this class
	private static ServerUIPlugin singleton;

	protected Map imageDescriptors = new HashMap();

	protected static List terminationWatches = new ArrayList();
	// server UI plugin id
	public static final String PLUGIN_ID = "org.eclipse.wst.server.ui";

	/**
	 * Create the ServerUIPlugin.
	 */
	public ServerUIPlugin() {
		super();
		singleton = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 *
	 * @return org.eclipse.wst.server.ui.internal.plugin.ServerUIPlugin
	 */
	public static ServerUIPlugin getInstance() {
		return singleton;
	}

	/**
	 * Returns the translated String found with the given key.
	 *
	 * @param key java.lang.String
	 * @return java.lang.String
	 */
	public static String getResource(String key) {
		try {
			return Platform.getResourceString(getInstance().getBundle(), key);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given arguments using java.text.MessageFormat.
	 *
	 * @param key java.lang.String
	 * @param arg java.lang.String
	 * @return java.lang.String
	 */
	public static String getResource(String key, String arg) {
		return getResource(key, new String[] {arg});
	}

	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given arguments using java.text.MessageFormat.
	 *
	 * @param key java.lang.String
	 * @param arguments java.lang.Object[]
	 * @return java.lang.String
	 */
	public static String getResource(String key, Object[] arguments) {
		try {
			String text = getResource(key);
			return MessageFormat.format(text, arguments);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * Convenience method for logging.
	 *
	 * @param status org.eclipse.core.runtime.IStatus
	 */
	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	/**
	 * Start up this plug-in.
	 *
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "----->----- Server UI plugin start ----->-----");
		super.start(context);
	
		IServerUIPreferences prefs = ServerUICore.getPreferences();
		((ServerUIPreferences) prefs).setDefaults();
	}
	
	/**
	 * Shuts down this plug-in and saves all plug-in state.
	 *
	 * @exception Exception
	 */
	public void stop(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "-----<----- Server UI plugin stop -----<-----");
		super.stop(context);
		
		ImageResource.dispose();
	}

	/**
	 * Adds a watch to this server. If it hasn't stopped in a
	 * reasonable amount of time, the user will be prompted to
	 * terminate the server.
	 *
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public static void addTerminationWatch(final Shell shell, final IServer server, final int mode) {
		if (terminationWatches.contains(server))
			return;
	
		terminationWatches.add(server);
	
		class TerminateThread extends Thread {
			public boolean alive = true;
			public IServerListener listener;
	
			public void run() {
				while (alive) {
					int delay = server.getServerType().getStartTimeout();
					if (mode == 1)
						delay = server.getServerType().getStopTimeout();
					else if (mode == 2)
						delay += server.getServerType().getStopTimeout();
					
					if (delay < 0)
						return;
					
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						// ignore
					}
					
					if (server.getServerState() == IServer.STATE_STOPPED)
						alive = false;
	
					if (alive) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								TerminationDialog dialog = new TerminationDialog(shell, server.getName());
								dialog.open();
								if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
									// only try calling terminate once. Also, make sure that it didn't stop while
									// the dialog was open
									if (server.getServerState() != IServer.STATE_STOPPED)
										server.stop(true);
									alive = false;
								}
							}
						});
					}
					if (!alive) {
						if (listener != null)
							server.removeServerListener(listener);
						terminationWatches.remove(server);
					}
				}
			}
		}
	
		final TerminateThread t = new TerminateThread();
		t.setDaemon(true);
		t.setPriority(Thread.NORM_PRIORITY - 2);
	
		// add listener to stop the thread if/when the server stops
		IServerListener listener = new ServerAdapter() {
			public void serverStateChange(IServer server2) {
				if (server2.getServerState() == IServer.STATE_STOPPED && t != null)
					t.alive = false;
			}
		};
		t.listener = listener;
		server.addServerListener(listener);
	
		t.start();
	}
}