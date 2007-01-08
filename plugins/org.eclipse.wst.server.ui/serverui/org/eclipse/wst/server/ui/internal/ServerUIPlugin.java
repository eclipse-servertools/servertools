/**********************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.*;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
import org.eclipse.wst.server.ui.internal.editor.IServerEditorInput;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorCore;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorInput;
import org.eclipse.wst.server.ui.internal.viewers.InitialSelectionProvider;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewRuntimeWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.osgi.framework.BundleContext;
/**
 * The server UI plugin class.
 */
public class ServerUIPlugin extends AbstractUIPlugin {
	protected static final String VIEW_ID = "org.eclipse.wst.server.ui.ServersView";

	// server UI plugin id
	public static final String PLUGIN_ID = "org.eclipse.wst.server.ui";
	
	protected static final String EXTENSION_SERVER_IMAGES = "serverImages";
	private static final String EXTENSION_WIZARD_FRAGMENTS = "wizardFragments";
	public static final String EXTENSION_EDITOR_PAGES = "editorPages";
	public static final String EXTENSION_EDITOR_PAGE_SECTIONS = "editorPageSections";

	//public static final byte START = 0;
	public static final byte STOP = 1;
	//public static final byte RESTART = 2;

	// singleton instance of this class
	private static ServerUIPlugin singleton;

	protected Map imageDescriptors = new HashMap();

	// cached copy of all runtime wizards
	private static Map wizardFragments;

	// cached initial selection provider
	private static InitialSelectionProvider selectionProvider;

	private static IRegistryChangeListener registryListener;

	protected static class RegistryChangeListener implements IRegistryChangeListener {
		public void registryChanged(IRegistryChangeEvent event) {
			IExtensionDelta[] deltas = event.getExtensionDeltas(ServerUIPlugin.PLUGIN_ID, EXTENSION_WIZARD_FRAGMENTS);
			if (deltas != null) {
				for (int i = 0; i < deltas.length; i++) {
					handleWizardFragmentDelta(deltas[i]);
				}
			}
			
			deltas = event.getExtensionDeltas(ServerUIPlugin.PLUGIN_ID, EXTENSION_SERVER_IMAGES);
			if (deltas != null) {
				for (int i = 0; i < deltas.length; i++) {
					ImageResource.handleServerImageDelta(deltas[i]);
				}
			}
			
			deltas = event.getExtensionDeltas(ServerUIPlugin.PLUGIN_ID, EXTENSION_EDITOR_PAGES);
			if (deltas != null) {
				for (int i = 0; i < deltas.length; i++) {
					ServerEditorCore.handleEditorPageFactoriesDelta(deltas[i]);
				}
			}
			
			deltas = event.getExtensionDeltas(ServerUIPlugin.PLUGIN_ID, EXTENSION_EDITOR_PAGE_SECTIONS);
			if (deltas != null) {
				for (int i = 0; i < deltas.length; i++) {
					ServerEditorCore.handleEditorPageSectionFactoriesDelta(deltas[i]);
				}
			}
		}
	}

	static class WizardFragmentData {
		String id;
		IConfigurationElement ce;
		WizardFragment fragment;
		
		public WizardFragmentData(String id, IConfigurationElement ce) {
			this.id = id;
			this.ce = ce;
		}
	}

	protected static List terminationWatches = new ArrayList();

	protected IServerLifecycleListener serverLifecycleListener = new IServerLifecycleListener() {
		public void serverAdded(IServer server) {
			server.addServerListener(serverListener);
			((Server) server).addPublishListener(publishListener);
		}

		public void serverChanged(IServer server) {
			// ignore
		}

		public void serverRemoved(IServer server) {
			server.removeServerListener(serverListener);
			((Server) server).removePublishListener(publishListener);
		}
	};

	protected static IServerListener serverListener = new IServerListener() {
		public void serverChanged(ServerEvent event) {
			int eventKind = event.getKind();
			// if (eventKind == (ServerEvent.SERVER_CHANGE | ServerEvent.STATE_CHANGE)) {
			if ((eventKind & ServerEvent.STATE_CHANGE) != 0) {
				showServersView(true);
			} else if ((eventKind & ServerEvent.SERVER_CHANGE) != 0) {
				showServersView(false);
			}
		}
	};

	protected static IPublishListener publishListener = new PublishAdapter() {
		public void publishStarted(IServer server) {
			showServersView(false);
		}

		public void publishFinished(IServer server, IStatus status) {
			showServersView(false);
		}
	};

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
	 * @see Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "----->----- Server UI plugin start ----->-----");
		super.start(context);
		
		ServerUIPreferences prefs = getPreferences();
		prefs.setDefaults();
		
		ServerCore.addServerLifecycleListener(serverLifecycleListener);
		
		InitializeJob job = new InitializeJob();
		job.schedule();
	}

	/**
	 * @see Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "-----<----- Server UI plugin stop -----<-----");
		super.stop(context);
		
		if (registryListener != null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			registry.removeRegistryChangeListener(registryListener);
		}
		
		ImageResource.dispose();
		
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				servers[i].removeServerListener(serverListener);
				((Server) servers[i]).removePublishListener(publishListener);
			}
		}
		
		ServerCore.removeServerLifecycleListener(serverLifecycleListener);
	}

	/**
	 * Adds a watch to this server. If it hasn't stopped in a
	 * reasonable amount of time, the user will be prompted to
	 * terminate the server.
	 *
	 * @param shell a shell
	 * @param server a server
	 * @param mode a debug mode
	 */
	public static void addTerminationWatch(final Shell shell, final IServer server, final int mode) {
		if (terminationWatches.contains(server))
			return;
		
		terminationWatches.add(server);
		
		class TerminateThread extends Thread {
			public boolean alive = true;
			public IServerListener listener;

			public TerminateThread() {
				super("Server Termination Thread");
			}

			public void run() {
				while (alive) {
					ServerType serverType = (ServerType) server.getServerType();
					int delay = serverType.getStartTimeout();
					if (mode == 1)
						delay = serverType.getStopTimeout();
					else if (mode == 2)
						delay += serverType.getStopTimeout();
					
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
		IServerListener listener = new IServerListener() {
			public void serverChanged(ServerEvent event) {
				int eventKind = event.getKind();
				IServer server2 = event.getServer();
				if (eventKind == (ServerEvent.SERVER_CHANGE | ServerEvent.STATE_CHANGE)) {
					if (server2.getServerState() == IServer.STATE_STOPPED && t != null)
						t.alive = false;
				}
			}
		};
		t.listener = listener;
		server.addServerListener(listener);
	
		t.start();
	}
	
	/**
	 * Returns the server that came from the given file, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * servers ({@link ServerCore#getServers()}) for the one with a matching
	 * location ({@link Server#getFile()}). The file may not be null.
	 *
	 * @param file a server file
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
				if (file.equals(((Server)servers[i]).getFile()))
					return servers[i];
			}
		}
		return null;
	}
	
	/**
	 * Returns an array of all known runtime instances of
	 * the given runtime type. This convenience method filters the list of known
	 * runtime ({@link ServerCore#getRuntimes()}) for ones with a matching
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
	 * @param shell a shell
	 * @param server a server
	 * @return boolean
	 */
	public static boolean promptIfDirty(Shell shell, IServer server) {
		if (server == null)
			return false;
		
		if (!(server instanceof IServerWorkingCopy))
			return true;

		String title = Messages.resourceDirtyDialogTitle;
		
		IServerWorkingCopy wc = (IServerWorkingCopy) server;
		if (wc.isDirty()) {
			String message = NLS.bind(Messages.resourceDirtyDialogMessage, server.getName());
			String[] labels = new String[] {Messages.resourceDirtyDialogContinue, IDialogConstants.CANCEL_LABEL};
			MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.INFORMATION, labels, 0);
	
			if (dialog.open() != 0)
				return false;
		}
	
		return true;
	}

	/**
	 * Show the Servers view. The Servers is never given focus.
	 * 
	 * @param bringToFront <code>true</code> to make the Servers view push to the top
	 *    of the z-order, and <code>false</code> to just highlight it
	 */
	protected static void showServersView(final boolean bringToFront) {
		if (!getPreferences().getShowOnActivity())
			return;
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					IWorkbench workbench = ServerUIPlugin.getInstance().getWorkbench();
					IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
	
					IWorkbenchPage page = workbenchWindow.getActivePage();
	
					IViewPart view2 = page.findView(VIEW_ID);
					
					if (view2 != null) {
						if (bringToFront)
							page.bringToTop(view2);
						else {
							IWorkbenchSiteProgressService wsps = (IWorkbenchSiteProgressService)
								view2.getSite().getAdapter(IWorkbenchSiteProgressService.class);
							wsps.warnOfContentChange();
						}
					} else
						page.showView(VIEW_ID);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error opening TCP/IP view", e);
				}
			}
		});
	}

	/**
	 * Returns true if the given server is already started in the given
	 * mode, or could be (re)started in the start mode.
	 * 
	 * @param server
	 * @param launchMode
	 * @return boolean
	 */
	public static boolean isCompatibleWithLaunchMode(IServer server, String launchMode) {
		if (server == null || launchMode == null)
			return false;
		
		int state = server.getServerState();
		if (state == IServer.STATE_STARTED && launchMode.equals(server.getMode()))
			return true;
		
		if (server.getServerType() != null && server.getServerType().supportsLaunchMode(launchMode))
			return true;
		return false;
	}

	/**
	 * Open the new runtime wizard.
	 * 
	 * @param shell
	 * @param type
	 * @param version
	 * @param runtimeTypeId
	 * @return true if a new runtime was created
	 */
	public static boolean showNewRuntimeWizard(Shell shell, final String type, final String version, final String runtimeTypeId) {
		WizardFragment fragment = new WizardFragment() {
			protected void createChildFragments(List list) {
				list.add(new NewRuntimeWizardFragment(type, version, runtimeTypeId));
				list.add(new WizardFragment() {
					public void performFinish(IProgressMonitor monitor) throws CoreException {
						WizardTaskUtil.saveRuntime(getTaskModel(), monitor);
					}
				});
			}
		};
		TaskWizard wizard = new TaskWizard(Messages.wizNewRuntimeWizardTitle, fragment);
		wizard.setForcePreviousAndNextButtons(true);
		ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
		return (dialog.open() == IDialogConstants.OK_ID);
	}
	
	/**
	 * Open the new runtime wizard.
	 * 
	 * @param shell
	 * @param runtimeTypeId
	 * @return true if a new runtime was created
	 */
	public static boolean showNewRuntimeWizard(Shell shell, final String runtimeTypeId) {
		IRuntimeType runtimeType = null;
		if (runtimeTypeId != null)
			runtimeType = ServerCore.findRuntimeType(runtimeTypeId);
		if (runtimeType != null) {
			try {
				final IRuntimeWorkingCopy runtime = runtimeType.createRuntime(null, null);
				TaskModel taskModel = new TaskModel();
				taskModel.putObject(TaskModel.TASK_RUNTIME, runtime);
				
				WizardFragment fragment = new WizardFragment() {
					protected void createChildFragments(List list) {
						list.add(getWizardFragment(runtimeTypeId));
						list.add(new WizardFragment() {
							public void performFinish(IProgressMonitor monitor) throws CoreException {
								WizardTaskUtil.saveRuntime(getTaskModel(), monitor);
							}
						});
					}
				};
				TaskWizard wizard = new TaskWizard(Messages.wizNewRuntimeWizardTitle, fragment, taskModel);
				wizard.setForcePreviousAndNextButtons(true);
				ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
				return (dialog.open() == IDialogConstants.OK_ID);
			} catch (Exception e) {
				return false;
			}
		}
		return showNewRuntimeWizard(shell, null, null, runtimeTypeId);
	}
	
	/**
	 * Open the new runtime wizard.
	 * @param shell
	 * @return true if a new runtime was created
	 */
	public static boolean showNewRuntimeWizard(Shell shell) {
		return ServerUIUtil.showNewRuntimeWizard(shell, null, null);
	}
	
	/**
	 * Returns the wizard fragment with the given id.
	 *
	 * @param typeId the server or runtime type id
	 * @return a wizard fragment, or <code>null</code> if none could be found
	 */
	public static WizardFragment getWizardFragment(String typeId) {
		if (typeId == null)
			return null;

		if (wizardFragments == null)
			loadWizardFragments();
		
		Iterator iterator = wizardFragments.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (typeId.equals(key)) {
				WizardFragmentData data = (WizardFragmentData) wizardFragments.get(key);
				return getWizardFragment(data);
			}
		}
		return null;
	}

	/**
	 * Load the wizard fragments.
	 */
	private static synchronized void loadWizardFragments() {
		if (wizardFragments != null)
			return;
		Trace.trace(Trace.CONFIG, "->- Loading .wizardFragments extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, EXTENSION_WIZARD_FRAGMENTS);

		Map map = new HashMap(cf.length);
		loadWizardFragments(cf, map);
		addRegistryListener();
		wizardFragments = map;
		
		Trace.trace(Trace.CONFIG, "-<- Done loading .wizardFragments extension point -<-");
	}

	/**
	 * Load wizard fragments.
	 */
	private static synchronized void loadWizardFragments(IConfigurationElement[] cf, Map map) {
		for (int i = 0; i < cf.length; i++) {
			try {
				String id = cf[i].getAttribute("typeIds");
				map.put(id, new WizardFragmentData(id, cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded wizardFragment: " + id);
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load wizardFragment: " + cf[i].getAttribute("id"), t);
			}
		}
	}

	/**
	 * Returns the initial selection provider.
	 *
	 * @return an initial selection provider, or <code>null</code> if none could be found
	 */
	public static InitialSelectionProvider getInitialSelectionProvider() {
		if (selectionProvider == null)
			loadInitialSelectionProvider();
		
		return selectionProvider;
	}

	/**
	 * Load the initial selection provider.
	 */
	private static synchronized void loadInitialSelectionProvider() {
		if (selectionProvider != null)
			return;
		
		Trace.trace(Trace.CONFIG, "->- Loading .initialSelectionProvider extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "initialSelectionProvider");
		
		if (cf.length == 1) {
			try {
				selectionProvider = (InitialSelectionProvider) cf[0].createExecutableExtension("class");
				Trace.trace(Trace.CONFIG, "  Loaded initialSelectionProvider: " + cf[0].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load initialSelectionProvider: " + cf[0].getAttribute("id"), t);
			}
		} else if (cf.length > 1)
			Trace.trace(Trace.WARNING, "More that one initial selection provider found - ignoring");
		else
			Trace.trace(Trace.CONFIG, "No initial selection provider found");
		
		if (selectionProvider == null)
			selectionProvider = new InitialSelectionProvider();
		
		Trace.trace(Trace.CONFIG, "-<- Done loading .initialSelectionProvider extension point -<-");
	}

	protected static WizardFragment getWizardFragment(WizardFragmentData fragment) {
		if (fragment == null)
			return null;
		
		if (fragment.fragment == null) {
			try {
				long time = System.currentTimeMillis();
				fragment.fragment = (WizardFragment) fragment.ce.createExecutableExtension("class");
				Trace.trace(Trace.PERFORMANCE, "ServerUIPlugin.getWizardFragment(): <" + (System.currentTimeMillis() - time) + "> " + fragment.ce.getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create wizardFragment: " + fragment.ce.getAttribute("id"), t);
			}
		}
		return fragment.fragment;
	}

	public static void runOnServer(Object object, String launchMode) {
		RunOnServerActionDelegate delegate = new RunOnServerActionDelegate();
		Action action = new Action() {
			// dummy action
		};
		if (object != null) {
			StructuredSelection sel = new StructuredSelection(object);
			delegate.selectionChanged(action, sel);
		} else
			delegate.selectionChanged(action, null);

		delegate.run(action);
	}

	public static synchronized void addRegistryListener() {
		if (registryListener != null)
			return;
		
		registryListener = new RegistryChangeListener();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		registry.addRegistryChangeListener(registryListener, ServerUIPlugin.PLUGIN_ID);
	}

	protected static void handleWizardFragmentDelta(IExtensionDelta delta) {
		if (wizardFragments == null) // not loaded yet
			return;
		
		IConfigurationElement[] cf = delta.getExtension().getConfigurationElements();
		
		Map map = new HashMap(wizardFragments);
		if (delta.getKind() == IExtensionDelta.ADDED) {
			loadWizardFragments(cf, map);
		} else {
			/*int size = wizardFragments.size();
			WizardFragment[] wf = new WizardFragment[size];
			wizardFragments.toArray(wf);
			int size2 = cf.length;
			
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size2; j++) {
					if (wf[i].getId().equals(cf[j].getAttribute("id"))) {
						wf[i].dispose();
						wizardFragments.remove(wf[i]);
					}
				}
			}*/
		}
		wizardFragments = map;
	}

	/**
	 * Utility method to tokenize a string into an array.
	 * 
	 * @param str a string to be parsed
	 * @param delim the delimiters
	 * @return an array containing the tokenized string
	 */
	public static String[] tokenize(String str, String delim) {
		if (str == null)
			return new String[0];
		
		List list = new ArrayList();
		
		StringTokenizer st = new StringTokenizer(str, delim);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s != null && s.length() > 0)
				list.add(s.trim());
		}
		
		String[] s = new String[list.size()];
		list.toArray(s);
		return s;
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
		if (server == null || launchable == null)
			return new IClient[0];
		
		ArrayList list = new ArrayList(5);
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

	public static Object[] getLaunchableAdapter(IServer server, IModuleArtifact moduleArtifact) throws CoreException {
		ILaunchableAdapter launchableAdapter = null;
		Object launchable = null;
		
		ILaunchableAdapter[] adapters = ServerPlugin.getLaunchableAdapters();
		if (adapters != null) {
			int size2 = adapters.length;
			IStatus lastStatus = null;
			for (int j = 0; j < size2; j++) {
				ILaunchableAdapter adapter = adapters[j];
				try {
					Object launchable2 = adapter.getLaunchable(server, moduleArtifact);
					Trace.trace(Trace.FINEST, "adapter= " + adapter + ", launchable= " + launchable2);
					if (launchable2 != null) {
						launchableAdapter = adapter;
						launchable = launchable2;
					}
				} catch (CoreException ce) {
					lastStatus = ce.getStatus();
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error in launchable adapter", e);
				}
			}
			if (launchable == null && lastStatus != null)
				throw new CoreException(lastStatus);
		}
		if (launchable == null) {
			launchableAdapter = new ILaunchableAdapter() {
				public String getId() {
					return "org.eclipse.wst.server.ui.launchable.adapter.default";
				}

				public Object getLaunchable(IServer server3, IModuleArtifact moduleArtifact2) throws CoreException {
					return "launchable";
				}
			};
			try {
				launchable = launchableAdapter.getLaunchable(server, moduleArtifact);
			} catch (CoreException ce) {
				// ignore
			}
		}
		return new Object[] { launchableAdapter, launchable };
	}
}