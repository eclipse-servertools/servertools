/*******************************************************************************
 * Copyright (c) 2003, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugOptionsListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.*;
import org.eclipse.wst.server.core.model.LaunchableAdapterDelegate;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.DeleteServerDialogExtension;
import org.eclipse.wst.server.ui.RemoveModuleMessageExtension;
import org.eclipse.wst.server.ui.editor.ServerEditorOverviewPageModifier;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
import org.eclipse.wst.server.ui.internal.editor.IServerEditorInput;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorCore;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorInput;
import org.eclipse.wst.server.ui.internal.provisional.AbstractServerLabelProvider;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;
import org.eclipse.wst.server.ui.internal.viewers.InitialSelectionProvider;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.internal.wizard.fragment.*;
import org.eclipse.wst.server.ui.wizard.ServerCreationWizardPageExtension;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
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

	public static class DefaultLaunchableAdapter extends LaunchableAdapterDelegate {
		public static final String ID = "org.eclipse.wst.server.ui.launchable.adapter.default";
		public Object getLaunchable(IServer server, IModuleArtifact moduleArtifact) {
			return "launchable";
		}
	}

	// singleton instance of this class
	private static ServerUIPlugin singleton;

	protected Map imageDescriptors = new HashMap();

	// cached copy of all runtime wizards
	private static Map<String, WizardFragmentData> wizardFragments;
	
	// Cached copy of all server wizard UI modifier
	private static List<ServerCreationWizardPageExtension> serverCreationWizardPageExtensions;
	
	// Cached copy of all server editor UI modifiers
	private static List<OverviewPageModifierFactory> serverEditorOverviewPageModifierFactories;
	
	// Cached copy of all delete dialog UI modifier
	private static List<DeleteServerDialogExtension> deleteServerDialogExtensions;
	
	// Cached copy of all the remove module message customization extensions
	private static List<RemoveModuleMessageExtension> removeModuleMessageExtensions;
	
	// Cached copy of server state label provider
	private static HashMap<String,AbstractServerLabelProvider> serverLabelProviders;
	// Create a default server state label provider
	private static AbstractServerLabelProvider defaultServerLabelProvider = new AbstractServerLabelProvider();
	
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

	protected static List<IServer> terminationWatches = new ArrayList<IServer>();

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
		return ServerUIPreferences.getInstance();
	}

	/**
	 * @see Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		super.start(context);

		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "----->----- Server UI plugin start ----->-----");
		}
		
		ServerCore.addServerLifecycleListener(serverLifecycleListener);

		// preload the server state label providers for faster UI response
		loadServerLabelProvider();
		
		InitializeJob job = new InitializeJob();
		job.schedule();

		// register the debug options listener
		final Hashtable<String, String> props = new Hashtable<String, String>(4);
		props.put(DebugOptions.LISTENER_SYMBOLICNAME, ServerUIPlugin.PLUGIN_ID);
		context.registerService(DebugOptionsListener.class.getName(), new Trace(), props);
	}

	/**
	 * @see Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-----<----- Server UI plugin stop -----<-----");
		}
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
					int delay = server.getStartTimeout() * 1000;
					if (mode == 1)
						delay = server.getStopTimeout() * 1000;
					else if (mode == 2)
						delay += server.getStopTimeout() * 1000;
					
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
					if (server2.getServerState() == IServer.STATE_STOPPED)
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
		List<IRuntime> list = new ArrayList<IRuntime>();
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
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error opening server editor", e);
			}
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
					if (workbenchWindow == null) {
						if (Trace.FINER) {
							Trace.trace(Trace.STRING_FINER, "No active workbench window");
						}
						return;
					}
					
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
					}
				} catch (Exception e) {
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Error opening Servers view", e);
					}
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
	 * @param shell a shell
	 * @param type a module type id
	 * @param version a module version id
	 * @param runtimeTypeId a runtime type id
	 * @return true if a new runtime was created
	 */
	public static boolean showNewRuntimeWizard(Shell shell, final String type, final String version, final String runtimeTypeId) {
		WizardFragment fragment = new WizardFragment() {
			protected void createChildFragments(List<WizardFragment> list) {
				list.add(new NewRuntimeWizardFragment(type, version, runtimeTypeId));
				list.add(WizardTaskUtil.SaveRuntimeFragment);
			}
		};
		TaskWizard wizard = new TaskWizard(Messages.wizNewRuntimeWizardTitle, fragment);
		wizard.setForcePreviousAndNextButtons(true);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		return (dialog.open() == IDialogConstants.OK_ID);
	}

	/**
	 * Open the new runtime wizard.
	 * 
	 * @param shell a shell
	 * @param runtimeTypeId a runtime type id
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
					protected void createChildFragments(List<WizardFragment> list) {
						list.add(getWizardFragment(runtimeTypeId));
						list.add(WizardTaskUtil.SaveRuntimeFragment);
					}
				};
				TaskWizard wizard = new TaskWizard(Messages.wizNewRuntimeWizardTitle, fragment, taskModel);
				wizard.setForcePreviousAndNextButtons(true);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				return (dialog.open() == IDialogConstants.OK_ID);
			} catch (Exception e) {
				return false;
			}
		}
		return showNewRuntimeWizard(shell, null, null, runtimeTypeId);
	}

	/**
	 * Open the new server wizard.
	 * 
	 * @param shell a shell
	 * @param typeId a module type id, or null for any module type
	 * @param versionId a module version, or null for any version
	 * @param serverTypeId a server runtime type, or null for any type
	 * @return <code>true</code> if a server was created, or
	 *    <code>false</code> otherwise
	 */
	public static boolean showNewServerWizard(Shell shell, final String typeId, final String versionId, final String serverTypeId) {
		WizardFragment fragment = new WizardFragment() {
			protected void createChildFragments(List<WizardFragment> list) {
				list.add(new NewServerWizardFragment(new ModuleType(typeId, versionId), serverTypeId));
				
				list.add(WizardTaskUtil.TempSaveRuntimeFragment);
				list.add(WizardTaskUtil.TempSaveServerFragment);
				
				list.add(new ModifyModulesWizardFragment());
				list.add(new TasksWizardFragment());
				
				list.add(WizardTaskUtil.SaveRuntimeFragment);
				list.add(WizardTaskUtil.SaveServerFragment);
				list.add(WizardTaskUtil.SaveHostnameFragment);
			}
		};
		
		TaskWizard wizard = new TaskWizard(Messages.wizNewServerWizardTitle, fragment);
		wizard.setForcePreviousAndNextButtons(true);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		return (dialog.open() == IDialogConstants.OK_ID);
	}

	/**
	 * Returns true if the given id has possible wizard fragments, and
	 * false otherwise.
	 *
	 * @param typeId the server or runtime type id
	 * @return true if the given id has possible wizard fragments, and
	 *    false otherwise
	 */
	public static boolean hasWizardFragment(String typeId) {
		if (typeId == null)
			return false;
		
		if (wizardFragments == null)
			loadWizardFragments();
		
		Iterator iterator = wizardFragments.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (typeId.equals(key))
				return true;
		}
		return false;
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
				WizardFragmentData data = wizardFragments.get(key);
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
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .wizardFragments extension point ->-");
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, EXTENSION_WIZARD_FRAGMENTS);
		
		Map<String, WizardFragmentData> map = new HashMap<String, WizardFragmentData>(cf.length);
		loadWizardFragments(cf, map);
		addRegistryListener();
		wizardFragments = map;
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .wizardFragments extension point -<-");
		}
	}

	/**
	 * Load wizard fragments.
	 */
	private static synchronized void loadWizardFragments(IConfigurationElement[] cf, Map<String, WizardFragmentData> map) {
		for (int i = 0; i < cf.length; i++) {
			try {
				String id = cf[i].getAttribute("typeIds");
				String[] ids = tokenize(id, ",");
				int size = ids.length;
				for (int j = 0; j < size; j++)
					map.put(ids[j], new WizardFragmentData(id, cf[i]));
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG, "  Loaded wizardFragment: " + id);
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "  Could not load wizardFragment: " + cf[i].getAttribute("id"), t);
				}
			}
		}
	}

	/**
	 * @deprecated - A computed default new runtime type based on what's
	 *             installed ends up being more annoying than useful (e.g.
	 *             always selection the Preview Server even if you never use
	 *             it).
	 * 
	 *             Returns the initial selection provider.
	 *
	 * @return an initial selection provider, or <code>null</code> if none
	 *         could be found
	 */
	public static InitialSelectionProvider getInitialSelectionProvider() {
		if (selectionProvider == null)
			loadInitialSelectionProvider();
		
		return selectionProvider;
	}

 	/**
	 * Returns the server label provider. There is always a server label provider for a server type
	 *
	 * @return the server label provider, if one is provider by the adopter otherwise it returns a default one
	 */
	public static AbstractServerLabelProvider getServerLabelProvider(String serverTypeId) {
		if (serverLabelProviders == null)
			loadServerLabelProvider();
		
		AbstractServerLabelProvider serverLabelProvider = serverLabelProviders.get(serverTypeId);
		
		// If there isn't one defined by an adopter, then bound the defaultServerLabelProvider to the list
		if (serverLabelProvider == null){
			serverLabelProvider = defaultServerLabelProvider;
			serverLabelProviders.put(serverTypeId, serverLabelProvider);
		}
		
		return serverLabelProvider; 
	}

	/**
	 * Returns the list of server creation wizard modifier.
	 *
	 * @return the list of server creation wizard modifier, or an empty list if none could be found
	 */
	public static List<ServerCreationWizardPageExtension> getServerCreationWizardPageExtensions() {
		if (serverCreationWizardPageExtensions == null)
			loadServerCreationWizardPageExtensions();

		return serverCreationWizardPageExtensions;
	}

 	/**
	 * Returns the list of server editor modifiers that are valid for this server type
	 *
	 * @return the list of server editor modifiers, or an empty list if none could be found
	 */
	public static List<ServerEditorOverviewPageModifier> getServerEditorOverviewPageModifiers(String serverType) {
		return getServerEditorOverviewPageModifiersImpl(serverType);
	}
	
	private static List<ServerEditorOverviewPageModifier> getServerEditorOverviewPageModifiersImpl(String serverType) {
		if (serverEditorOverviewPageModifierFactories == null)
			loadServerEditorOverviewPageModifiers();
		ArrayList<ServerEditorOverviewPageModifier> pageModifierImplLst = new ArrayList<ServerEditorOverviewPageModifier>();
		Iterator<OverviewPageModifierFactory> dIterator = serverEditorOverviewPageModifierFactories.iterator();
		OverviewPageModifierFactory curFactory;
		while(dIterator.hasNext()) {
			curFactory = dIterator.next();
			if( curFactory != null && (serverType == null || curFactory.supportsType(serverType))) {
				pageModifierImplLst.add(curFactory.createModifier());
			}
		}
		return pageModifierImplLst;
	}
	
	private static class OverviewPageModifierFactory implements Comparable<OverviewPageModifierFactory> {
		IConfigurationElement element;
		String[] serverTypes = null;
		int order = 10;
		OverviewPageModifierFactory(IConfigurationElement element) {
			this.element = element;
			init();
		}
		private void init() {
			String attr = element.getAttribute("serverTypes");
			serverTypes = (attr == null || attr.length() == 0) ? null : attr.split(",");
			
			String w = element.getAttribute("order");
			if( w != null ) {
				try {
					order = Integer.parseInt(w);
				} catch (NumberFormatException e) {
					// Do nothing.
				}
			}
		}
		ServerEditorOverviewPageModifier createModifier() {
			try {
				ServerEditorOverviewPageModifier curExtension = (ServerEditorOverviewPageModifier)element.createExecutableExtension("class");
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG,
							"  New instance of .serverEditorOverviewPageModifier: " + element.getAttribute("id")
									+ ", loaded class=" + curExtension);
				}
				return curExtension;
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE,
							"  Could not create new instance .serverEditorOverviewPageModifier: " + element.getAttribute("id"), t);
				}
			}
			return null;
		}
		
		boolean supportsType(String serverType) {
			return ServerPlugin.contains(serverTypes, serverType);
		}

		public int compareTo(OverviewPageModifierFactory factory) {
			return order - factory.order;
		}
	}

	/**
	 * Returns the list of delete server dialog modifiers.
	 *
	 * @return the list of delete server dialog modifiers, or an empty list if none could be found
	 */
	public static List<DeleteServerDialogExtension> getDeleteServerDialogExtensions() {
		if (deleteServerDialogExtensions == null)
			loadDeleteServerDialogExtensions();

		return deleteServerDialogExtensions;
	}
	
	/**
	 * Returns the list of remove module message confirmation extensions
	 * @return the list of remove module message confirmation extensions, or an empty list if none could be found
	 */
	public static List<RemoveModuleMessageExtension> getRemoveModuleMessageExtensions() {
		if (removeModuleMessageExtensions == null) {
		    loadRemoveModuleMessageExtensions();
		}
		return removeModuleMessageExtensions;
	}

	/**
	 * Load the Server creation wizard page modifiers.
	 */
	private static synchronized void loadServerCreationWizardPageExtensions() {
		if (serverCreationWizardPageExtensions != null)
			return;
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .serverCreationWizardPageExtension extension point ->-");
		}
		serverCreationWizardPageExtensions = new ArrayList<ServerCreationWizardPageExtension>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "serverCreationWizardPageExtension");
		
		for (IConfigurationElement curConfigElement: cf) {
			try {
				// Create the class here already since the usage of the server wizard page will need to use all the extensions
				// in all the calls.  Therefore, there is no need for lazy loading here.
				ServerCreationWizardPageExtension curExtension = (ServerCreationWizardPageExtension)curConfigElement.createExecutableExtension("class");
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG,
							"  Loaded .serverCreationWizardPageExtension: " + cf[0].getAttribute("id")
									+ ", loaded class=" + curExtension);
				}
				if (curExtension != null)
					serverCreationWizardPageExtensions.add(curExtension);

			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE,
							"  Could not load .serverCreationWizardPageExtension: " + cf[0].getAttribute("id"), t);
				}
			}
		}
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .serverCreationWizardPageExtension extension point -<-");
		}
	}

	/**
	 * Load the Server editor page modifiers.
	 */
	private static synchronized void loadServerEditorOverviewPageModifiers() {
		if (serverEditorOverviewPageModifierFactories != null)
			return;
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .serverEditorOverviewPageModifier extension point ->-");
		}
		serverEditorOverviewPageModifierFactories = new ArrayList<OverviewPageModifierFactory>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "serverEditorOverviewPageModifier");
		
		for (IConfigurationElement curConfigElement: cf) {
			try {
				ServerEditorOverviewPageModifier curExtension = (ServerEditorOverviewPageModifier)curConfigElement.createExecutableExtension("class");
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG,
							"  Loaded .serverEditorOverviewPageModifier: " + cf[0].getAttribute("id")
									+ ", loaded class=" + curExtension);
				}
				if (curExtension != null)
					serverEditorOverviewPageModifierFactories.add(new OverviewPageModifierFactory(curConfigElement));
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE,
							"  Could not load .serverEditorOverviewPageModifier: " + cf[0].getAttribute("id"), t);
				}
			}
		}
		Collections.sort(serverEditorOverviewPageModifierFactories);
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .serverEditorOverviewPageModifier extension point -<-");
		}
	}
	
	/**
	 * Load the Delete server dialog modifiers.
	 */
	private static synchronized void loadDeleteServerDialogExtensions() {
		if (deleteServerDialogExtensions != null)
			return;
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .deleteServerDialogExtension extension point ->-");
		}
		deleteServerDialogExtensions = new ArrayList<DeleteServerDialogExtension>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "deleteServerDialogExtension");
		
		for (IConfigurationElement curConfigElement: cf) {
			try {
				DeleteServerDialogExtension curExtension = (DeleteServerDialogExtension)curConfigElement.createExecutableExtension("class");
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG,
							"  Loaded .deleteServerDialogExtension: " + cf[0].getAttribute("id")
									+ ", loaded class=" + curExtension);
				}
				if (curExtension != null)
					deleteServerDialogExtensions.add(curExtension);

			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE,
							"  Could not load .deleteServerDialogExtension: " + cf[0].getAttribute("id"), t);
				}
			}
		}
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .deleteServerDialogExtension extension point -<-");
		}
	}
	
	/**
	 * Load the remove module message customization extensions.
	 */
	private static synchronized void loadRemoveModuleMessageExtensions() {
		if (removeModuleMessageExtensions != null)
			return;
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .removeModuleMessageExtensions extension point ->-");
		}
		removeModuleMessageExtensions = new ArrayList<RemoveModuleMessageExtension>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "removeModuleMessageExtension");
		
		for (IConfigurationElement curConfigElement: cf) {
			try {
				RemoveModuleMessageExtension curExtension = (RemoveModuleMessageExtension)curConfigElement.createExecutableExtension("class");
				String serverTypeId = curConfigElement.getAttribute("serverTypeId");
				if (serverTypeId != null) {
					curExtension.setServerTypeId(serverTypeId);
				}
				
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG,
							"  Loaded .removeModuleMessageExtension: " + curConfigElement.getAttribute("id")
									+ ", loaded class=" + curExtension);
				}
				if (curExtension != null)
					removeModuleMessageExtensions.add(curExtension);

			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE,
							"  Could not load .removeModuleMessageExtension: " + curConfigElement.getAttribute("id"), t);
				}
			}
		}
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .removeModuleMessageExtension extension point -<-");
		}
	}
	
	/**
	 * Load the Server Label Providers. If there is two implemented for a ServerType the adopters ServerLabelProviders
	 * will be ignored an a default will be returned
	 */
	private static synchronized void loadServerLabelProvider() {
		if (serverLabelProviders != null)
			return;
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .serverLabelProvider extension point ->-");
		}
		serverLabelProviders = new HashMap<String,AbstractServerLabelProvider>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "serverLabelProvider");
		
		for (IConfigurationElement curConfigElement: cf) {
			// load the extension in a safe enviroment. If there are NPEs or missconfigurations they will be caught by try/catch
			String[] exServerTypes = ServerPlugin.tokenize(curConfigElement.getAttribute("serverTypes"), ",");
			for (String exServerType : exServerTypes) {
				try {
					if (serverLabelProviders.containsKey(exServerType)){
						if (Trace.WARNING) {
							Trace.trace(Trace.STRING_WARNING, "More that one .serverLabelProvider found - ignoring");
						}
						serverLabelProviders.put(exServerType, defaultServerLabelProvider);
					}
					else{
						AbstractServerLabelProvider exClass = (AbstractServerLabelProvider)curConfigElement.createExecutableExtension("class");
						if (Trace.CONFIG) {
							Trace.trace(Trace.STRING_CONFIG,
									"  Loaded .serverLabelProvider: " + curConfigElement.getAttribute("id")
											+ ", loaded class=" + exClass);
						}
						serverLabelProviders.put(exServerType, exClass); 
					}
				} catch (Throwable t) {
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE,
								"  Could not load .serverLabelProvider: " + curConfigElement.getAttribute("id"), t);
					}
				}
			}
		}		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .serverLabelProvider extension point -<-");
		}
	}	
	
	/**
	 * Load the initial selection provider.
	 */
	private static synchronized void loadInitialSelectionProvider() {
		if (selectionProvider != null)
			return;
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .initialSelectionProvider extension point ->-");
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "initialSelectionProvider");
		
		if (cf.length == 1) {
			try {
				selectionProvider = (InitialSelectionProvider) cf[0].createExecutableExtension("class");
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG, "  Loaded initialSelectionProvider: " + cf[0].getAttribute("id"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE,
							"  Could not load initialSelectionProvider: " + cf[0].getAttribute("id"), t);
				}
			}
		} else if (cf.length > 1)
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "More that one initial selection provider found - ignoring");
			}
		else if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "No initial selection provider found");
		}
		
		if (selectionProvider == null)
			selectionProvider = new InitialSelectionProvider();
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .initialSelectionProvider extension point -<-");
		}
	}

	protected static WizardFragment getWizardFragment(WizardFragmentData fragment) {
		if (fragment == null)
			return null;
		
		if (fragment.fragment == null) {
			try {
				long time = System.currentTimeMillis();
				fragment.fragment = (WizardFragment) fragment.ce.createExecutableExtension("class");
				if (Trace.PERFORMANCE) {
					Trace.trace(Trace.STRING_PERFORMANCE,
							"ServerUIPlugin.getWizardFragment(): <" + (System.currentTimeMillis() - time) + "> "
									+ fragment.ce.getAttribute("id"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE,
							"Could not create wizardFragment: " + fragment.ce.getAttribute("id"), t);
				}
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
		
		Map<String, WizardFragmentData> map = new HashMap<String, WizardFragmentData>(wizardFragments);
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
		
		List<String> list = new ArrayList<String>();
		
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
		
		ArrayList<IClient> list = new ArrayList<IClient>(5);
		IClient[] clients = ServerPlugin.getClients();
		if (clients != null) {
			int size = clients.length;
			for (int i = 0; i < size; i++) {
				if (Trace.FINEST) {
					Trace.trace(Trace.STRING_FINEST, "client= " + clients[i]);
				}
				if (clients[i].supports(server, launchable, launchMode))
					list.add(clients[i]);
			}
		}
		
		IClient[] clients2 = new IClient[list.size()];
		list.toArray(clients2);
		return clients2;
	}

	public static Object[] getLaunchableAdapter(IServer server, IModuleArtifact moduleArtifact) throws CoreException {
		ILaunchableAdapter[] adapters = ServerPlugin.getLaunchableAdapters();
		if (adapters != null) {
			int size2 = adapters.length;
			IStatus lastStatus = null;
			for (int j = 0; j < size2; j++) {
				ILaunchableAdapter adapter = adapters[j];
				try {
					Object launchable2 = adapter.getLaunchable(server, moduleArtifact);
					if (Trace.FINEST) {
						Trace.trace(Trace.STRING_FINEST, "adapter= " + adapter + ", launchable= " + launchable2);
					}
					if (launchable2 != null)
						return new Object[] { adapter, launchable2 };
				} catch (CoreException ce) {
					lastStatus = ce.getStatus();
				} catch (Exception e) {
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Error in launchable adapter", e);
					}
				}
			}
			if (lastStatus != null)
				throw new CoreException(lastStatus);
		}
		
		// backup
		ILaunchableAdapter launchableAdapter = ServerPlugin.findLaunchableAdapter(DefaultLaunchableAdapter.ID);
		Object launchable = launchableAdapter.getLaunchable(server, moduleArtifact);
		return new Object[] { launchableAdapter, launchable };
	}

	public static Object[] adaptLabelChangeObjects(Object[] obj) {
		if (obj == null)
			return obj;
		
		List<Object> list = new ArrayList<Object>();
		int size = obj.length;
		for (int i = 0; i < size; i++) {
			if (obj[i] instanceof IModule) {
				list.add(obj[i]);
			} else if (obj[i] instanceof IServer) {
				list.add(obj[i]);
			} else if (obj[i] instanceof ModuleServer) {
				list.add(obj[i]);
			} else if (obj[i] instanceof IProject) {
				IProject proj = (IProject) obj[i];
				IModule[] m = ServerUtil.getModules(proj);
				int size2 = m.length;
				for (int j = 0; j < size2; j++)
					list.add(m[j]);
			}
		}
		
		Object[] o = new Object[list.size()];
		list.toArray(o);
		return o;
	}
	
	public static RemoveModuleMessageExtension getRemoveModuleMessageExtension(String targetServerTypeId) {
		List<RemoveModuleMessageExtension> extensions = getRemoveModuleMessageExtensions();
		for (RemoveModuleMessageExtension messageExtension : extensions) {
			String serverTypeId = messageExtension.getServerTypeId();
			// regex support serverTypeIdValue.* and also support serverTypeIdValue* 
			if (serverTypeId != null) {
				int length = serverTypeId.length();
				if (serverTypeId.equals(targetServerTypeId) || targetServerTypeId.matches(serverTypeId)
					|| (serverTypeId.endsWith("*") && length > 1 && targetServerTypeId.startsWith(serverTypeId.substring(0, length - 1)))) {
					// We found the extension for this server type
					return messageExtension;
				}
			}
		}
		return null;
	}
}