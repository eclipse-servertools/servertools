/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.util.ServerAdapter;
import org.eclipse.wst.server.ui.editor.IServerEditorInput;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorInput;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
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
	 * Return the UI preferences.
	 * 
	 * @return ServerUIPreferences
	 */
	public static ServerUIPreferences getPreferences() {
		return new ServerUIPreferences();
	}

	/**
	 * Start up this plug-in.
	 *
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "----->----- Server UI plugin start ----->-----");
		super.start(context);
	
		ServerUIPreferences prefs = getPreferences();
		prefs.setDefaults();
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
	 * @param server org.eclipse.wst.server.core.IServer
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
	
	//protected static final String MODULE_ARTIFACT_CLASS = "org.eclipse.wst.server.core.IModuleArtifact";
	
	/**
	 * Returns the module artifact for the given object, or null if a module artifact
	 * can't be found.
	 *
	 * @return the module artifact
	 */
	/*public static boolean hasModuleArtifact(Object adaptable) {
		if (adaptable instanceof IModuleArtifact)
			return true;
		
		return Platform.getAdapterManager().hasAdapter(adaptable, MODULE_ARTIFACT_CLASS);
	}*/
	
	/**
	 * Returns the module artifact for the given object, or null if a module artifact
	 * can't be found.
	 *
	 * @return the module artifact
	 */
	/*public static IModuleArtifact getModuleArtifact(Object adaptable) {
		if (adaptable instanceof IModuleArtifact)
			return (IModuleArtifact) adaptable;
		
		if (Platform.getAdapterManager().hasAdapter(adaptable, MODULE_ARTIFACT_CLASS)) {
			return (IModuleArtifact) Platform.getAdapterManager().getAdapter(adaptable, MODULE_ARTIFACT_CLASS);
		}
		
		return null;
	}*/

	/**
	 * Returns the module artifact for the given object, or null if a module artifact
	 * can't be found.
	 *
	 * @return the module artifact
	 */
	/*public static IModuleArtifact loadModuleArtifact(Object obj) {
		if (obj instanceof IModuleArtifact)
			return (IModuleArtifact) obj;
		
		if (Platform.getAdapterManager().hasAdapter(obj, MODULE_ARTIFACT_CLASS)) {
			return (IModuleArtifact) Platform.getAdapterManager().loadAdapter(obj, MODULE_ARTIFACT_CLASS);
		}
		
		return null;
	}*/

	// cached copy of all module artifact adapters
	private static List moduleArtifactAdapters;

	/**
	 * Returns an array of all module artifact adapters.
	 *
	 * @return
	 */
	protected static ModuleArtifactAdapter[] getModuleArtifactAdapters() {
		if (moduleArtifactAdapters == null)
			loadModuleArtifactAdapters();
		
		ModuleArtifactAdapter[] moa = new ModuleArtifactAdapter[moduleArtifactAdapters.size()];
		moduleArtifactAdapters.toArray(moa);
		return moa;
	}
	
	/**
	 * Load the module artifact adapters extension point.
	 */
	private static synchronized void loadModuleArtifactAdapters() {
		if (moduleArtifactAdapters != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .moduleArtifactAdapters extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "moduleArtifactAdapters");

		int size = cf.length;
		moduleArtifactAdapters = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				moduleArtifactAdapters.add(new ModuleArtifactAdapter(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded moduleArtifactAdapter: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load moduleArtifactAdapter: " + cf[i].getAttribute("id"), t);
			}
		}
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .moduleArtifactAdapters extension point -<-");
	}
	
	/**
	 * Returns a module artifact for the given object.
	 *
	 * @return IModuleArtifact
	 */
	public static boolean hasModuleArtifact(Object obj) {
		Trace.trace(Trace.FINEST, "ServerUIPlugin.hasModuleArtifact() " + obj);
		ModuleArtifactAdapter[] adapters = getModuleArtifactAdapters();
		if (adapters != null) {
			int size = adapters.length;
			for (int i = 0; i < size; i++) {
				try {
					if (adapters[i].isEnabled(obj)) {
						Trace.trace(Trace.FINER, "Run On Server for " + obj + " is enabled by " + adapters[i].getId());
						return true;
					}
				} catch (CoreException ce) {
					Trace.trace(Trace.WARNING, "Could not use moduleArtifactAdapter", ce);
				}
			}
		}
		
		return false;
	}
	
	protected static final String MODULE_ARTIFACT_CLASS = "org.eclipse.wst.server.core.IModuleArtifact";

	public static IModuleArtifact getModuleArtifact(Object obj) {
		Trace.trace(Trace.FINEST, "ServerUIPlugin.getModuleArtifact() " + obj);
		ModuleArtifactAdapter[] adapters = getModuleArtifactAdapters();
		if (adapters != null) {
			int size = adapters.length;
			for (int i = 0; i < size; i++) {
				try {
					if (adapters[i].isEnabled(obj)) {
						if (Platform.getAdapterManager().hasAdapter(obj, MODULE_ARTIFACT_CLASS)) {
							return (IModuleArtifact) Platform.getAdapterManager().getAdapter(obj, MODULE_ARTIFACT_CLASS);
						}
					}
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Could not use moduleArtifactAdapter " + adapters[i], e);
				}
			}
		}
		
		return null;
	}

	public static IModuleArtifact loadModuleArtifact(Object obj) {
		Trace.trace(Trace.FINEST, "ServerUIPlugin.loadModuleArtifact() " + obj);
		ModuleArtifactAdapter[] adapters = getModuleArtifactAdapters();
		if (adapters != null) {
			int size = adapters.length;
			for (int i = 0; i < size; i++) {
				try {
					if (adapters[i].isEnabled(obj)) {
						if (Platform.getAdapterManager().hasAdapter(obj, MODULE_ARTIFACT_CLASS)) {
							return (IModuleArtifact) Platform.getAdapterManager().loadAdapter(obj, MODULE_ARTIFACT_CLASS);
						}
					}
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Could not use moduleArtifactAdapter " + adapters[i], e);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the server that came from the given file, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * servers ({@link #getServers()}) for the one with a matching
	 * location ({@link IServer#getFile()}). The file may not be null.
	 *
	 * @param a server file
	 * @return the server instance, or <code>null</code> if 
	 * there is no server associated with the given file
	 */
	public static IServer findServer(IFile file) {
		if (file == null)
			throw new IllegalArgumentException();
		
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				if (file.equals(servers[i].getFile()))
					return servers[i];
			}
		}
		return null;
	}
	
	/**
	 * Returns an array of all known runtime instances of
	 * the given runtime type. This convenience method filters the list of known
	 * runtime ({@link #getRuntimes()}) for ones with a matching
	 * runtime type ({@link IRuntime#getRuntimeType()}). The array will not
	 * contain any working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @param runtimeType the runtime type
	 * @return a possibly-empty list of runtime instances {@link IRuntime}
	 * of the given runtime type
	 */
	public static IRuntime[] getRuntimes(IRuntimeType runtimeType) {
		List list = new ArrayList();
		IRuntime[] runtimes = ServerCore.getRuntimes();
		if (runtimes != null) {
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				if (runtimes[i].getRuntimeType() != null && runtimes[i].getRuntimeType().equals(runtimeType))
					list.add(runtimes[i]);
			}
		}
		
		IRuntime[] r = new IRuntime[list.size()];
		list.toArray(r);
		return r;
	}
	
	/**
	 * Open the given server with the server editor.
	 *
	 * @param server
	 */
	public static void editServer(IServer server) {
		if (server == null)
			return;

		editServer(server.getId());
	}

	/**
	 * Open the given server id with the server editor.
	 *
	 * @param serverId
	 */
	protected static void editServer(String serverId) {
		if (serverId == null)
			return;

		IWorkbenchWindow workbenchWindow = ServerUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();

		try {
			IServerEditorInput input = new ServerEditorInput(serverId);
			page.openEditor(input, IServerEditorInput.EDITOR_ID);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error opening server editor", e);
		}
	}
	
	/**
	 * Use the preference to prompt the user to save dirty editors, if applicable.
	 * 
	 * @return boolean  - Returns false if the user cancelled the operation
	 */
	public static boolean saveEditors() {
		byte b = ServerUIPlugin.getPreferences().getSaveEditors();
		if (b == ServerUIPreferences.SAVE_EDITORS_NEVER)
			return true;
		return ServerUIPlugin.getInstance().getWorkbench().saveAllEditors(b == ServerUIPreferences.SAVE_EDITORS_PROMPT);			
	}
	
	/**
	 * Prompts the user if the server is dirty. Returns true if the server was
	 * not dirty or if the user decided to continue anyway. Returns false if
	 * the server is dirty and the user chose to cancel the operation.
	 *
	 * @return boolean
	 */
	public static boolean promptIfDirty(Shell shell, IServer server) {
		if (server == null)
			return false;
		
		if (!(server instanceof IServerWorkingCopy))
			return true;

		String title = ServerUIPlugin.getResource("%resourceDirtyDialogTitle");
		
		IServerWorkingCopy wc = (IServerWorkingCopy) server;
		if (wc.isDirty()) {
			String message = ServerUIPlugin.getResource("%resourceDirtyDialogMessage", server.getName());
			String[] labels = new String[] {ServerUIPlugin.getResource("%resourceDirtyDialogContinue"), IDialogConstants.CANCEL_LABEL};
			MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.INFORMATION, labels, 0);
	
			if (dialog.open() != 0)
				return false;
		}
	
		return true;
	}
}