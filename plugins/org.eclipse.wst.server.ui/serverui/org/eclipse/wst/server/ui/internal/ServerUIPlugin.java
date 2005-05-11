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
package org.eclipse.wst.server.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.*;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
import org.eclipse.wst.server.ui.internal.editor.IServerEditorInput;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorInput;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.InputWizardFragment;
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
import org.osgi.framework.BundleContext;
/**
 * The server UI plugin class.
 */
public class ServerUIPlugin extends AbstractUIPlugin {
	protected static final String VIEW_ID = "org.eclipse.wst.server.ui.ServersView";
	
	// server UI plugin id
	public static final String PLUGIN_ID = "org.eclipse.wst.server.ui";

	//public static final byte START = 0;
	public static final byte STOP = 1;
	//public static final byte RESTART = 2;
	
	// singleton instance of this class
	private static ServerUIPlugin singleton;

	protected Map imageDescriptors = new HashMap();
	
	// cached copy of all runtime wizards
	private static Map wizardFragments;
	
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
			if (eventKind == (ServerEvent.SERVER_CHANGE | ServerEvent.STATE_CHANGE)) {
				showServersView();
			}
		}
	};
	
	protected static IPublishListener publishListener = new PublishAdapter() {
		public void publishStarted(IServer server) {
			showServersView();
		}

		public void publishFinished(IServer server, IStatus status) {
			showServersView();
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
	 * Returns the translated String found with the given key.
	 *
	 * @param key java.lang.String
	 * @return java.lang.String
	 */
	public static String getResource2(String key) {
		try {
			return Platform.getResourceString(getInstance().getBundle(), key);
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
	 * @see Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "----->----- Server UI plugin start ----->-----");
		super.start(context);
	
		ServerUIPreferences prefs = getPreferences();
		prefs.setDefaults();
		
		ServerCore.addServerLifecycleListener(serverLifecycleListener);
		
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				servers[i].addServerListener(serverListener);
				((Server) servers[i]).addPublishListener(publishListener);
			}
		}
	}

	/**
	 * @see Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "-----<----- Server UI plugin stop -----<-----");
		super.stop(context);
		
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
	
	protected static void showServersView() {
		if (!getPreferences().getShowOnActivity())
			return;
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					IWorkbench workbench = ServerUIPlugin.getInstance().getWorkbench();
					IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
	
					IWorkbenchPage page = workbenchWindow.getActivePage();
	
					IViewPart view2 = page.findView(VIEW_ID);
					
					if (view2 != null)
						page.bringToTop(view2);
					else
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

		if (server.getServerType().supportsLaunchMode(launchMode))
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
				WizardFragment fragment = new WizardFragment() {
					protected void createChildFragments(List list) {
						list.add(new InputWizardFragment(TaskModel.TASK_RUNTIME, runtime));
						list.add(getWizardFragment(runtimeTypeId));
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
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "wizardFragments");

		int size = cf.length;
		wizardFragments = new HashMap(size);
		for (int i = 0; i < size; i++) {
			try {
				String id = cf[i].getAttribute("typeIds");
				wizardFragments.put(id, new WizardFragmentData(id, cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded wizardFragment: " + id);
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load wizardFragment: " + cf[i].getAttribute("id"), t);
			}
		}
		
		Trace.trace(Trace.CONFIG, "-<- Done loading .wizardFragments extension point -<-");
	}

	protected static WizardFragment getWizardFragment(WizardFragmentData fragment) {
		if (fragment == null)
			return null;
	
		if (fragment.fragment == null) {
			try {
				fragment.fragment = (WizardFragment) fragment.ce.createExecutableExtension("class");
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
}