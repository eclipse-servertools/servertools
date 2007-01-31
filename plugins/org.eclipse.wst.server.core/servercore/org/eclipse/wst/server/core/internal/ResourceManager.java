/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.io.ByteArrayInputStream;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;
/**
 * ResourceManager handles the mappings between resources
 * and servers or server configurations, and creates
 * notification of servers or server configurations
 * being added and removed.
 * 
 * <p>Servers and server configurations may be a single
 * resource, or they may be a folder that contains a group
 * of files. Folder-resource may not contain other servers
 * or configurations.</p>
 */
public class ResourceManager {
	private static final String SERVER_DATA_FILE = "servers.xml";
	
	private static final byte EVENT_ADDED = 0;
	private static final byte EVENT_CHANGED = 1;
	private static final byte EVENT_REMOVED = 2;

	private static ResourceManager instance;

	// currently active runtimes and servers
	protected List runtimes;
	protected List servers;

	// lifecycle listeners
	protected transient List runtimeListeners;
	protected transient List serverListeners;

	// cache for disposing servers & runtimes
	protected List activeBundles;

	// resource change listeners
	private IResourceChangeListener resourceChangeListener;
	private Preferences.IPropertyChangeListener pcl;
	protected boolean ignorePreferenceChanges = false;

	protected List moduleServerEventHandlers;
	protected List moduleServerEventHandlerIndexes;

	private static boolean initialized;
	private static boolean initializing;

	/**
	 * Server resource change listener.
	 * 
	 * Resource listener - tracks changes on server resources so that
	 * we can reload/drop server instances and configurations that
	 * may change outside of our control.
	 * Listens for two types of changes:
	 * 1. Servers or configurations being added or removed
	 *    from their respective folders. (in the future, including
	 *    the addition or removal of a full server project, which
	 *    we currently can't listen for because there is no nature
	 *    attached to the project at this point - OTI defect)
	 * 2. Projects being deleted.
	 */
	protected class ServerResourceChangeListener implements IResourceChangeListener {
		/**
		 * Create a new ServerResourceChangeListener.
		 */
		public ServerResourceChangeListener() {
			super();
		}

		/**
		 * Listen for projects being added or removed and act accordingly.
		 * 
		 * @param event org.eclipse.core.resources.IResourceChangeEvent
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			if (delta == null)
				return;
			
			// ignore clean builds
			if (event.getBuildKind() == IncrementalProjectBuilder.CLEAN_BUILD)
				return;
			
			Trace.trace(Trace.RESOURCES, "->- ServerResourceChangeListener responding to resource change: " + event.getType() + " ->-");
			IResourceDelta[] children = delta.getAffectedChildren();
			if (children != null) {
				int size = children.length;
				for (int i = 0; i < size; i++) {
					IResource resource = children[i].getResource();
					if (resource != null && resource instanceof IProject) {
						projectChanged((IProject) resource, children[i]);
					}
				}
			}
			
			// search for changes to any project using a visitor
			try {
				delta.accept(new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta visitorDelta) {
						IResource resource = visitorDelta.getResource();

						// only respond to project changes
						if (resource != null && resource instanceof IProject) {
							publishHandleProjectChange(visitorDelta);
							return false;
						}
						return true;
					}
				});
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error responding to resource change", e);
			}
			
			Trace.trace(Trace.RESOURCES, "-<- Done ServerResourceChangeListener responding to resource change -<-");
		}

		/**
		 * React to a change within a possible server project.
		 *
		 * @param delta org.eclipse.core.resources.IResourceDelta
		 */
		protected void projectChanged(IProject project, IResourceDelta delta) {
			if (!ServerPlugin.getProjectProperties(project).isServerProject()) {
				Trace.trace(Trace.RESOURCES, "Not a server project: " + project.getName());
				return;
			}
			
			IResourceDelta[] children = delta.getAffectedChildren();
	
			int size = children.length;
			for (int i = 0; i < size; i++) {
				IResourceDelta child = children[i];

				// look for servers and server configurations
				try {
					child.accept(new IResourceDeltaVisitor() {
						public boolean visit(IResourceDelta delta2) {
							return handleResourceDelta(delta2);
						}
					});
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error responding to resource change", e);
				}
			}
		}
	}

	/**
	 * Cannot directly create a ResourceManager. Use
	 * ServersCore.getResourceManager().
	 */
	private ResourceManager() {
		super();
		instance = this;
	}

	/**
	 * Execute the server startup extension points.
	 */
	private static synchronized void executeStartups() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .startup extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "internalStartup");
		
		int size = cf.length;
		for (int i = 0; i < size; i++) {
			try {
				IStartup startup = (IStartup) cf[i].createExecutableExtension("class");
				try {
					startup.startup();
				} catch (Exception ex) {
					Trace.trace(Trace.SEVERE, "Startup failed" + startup.toString(), ex);
				}
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded startup: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load startup: " + cf[i].getAttribute("id"), t);
			}
		}
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .startup extension point -<-");
	}

	protected synchronized void init() {
		if (initialized || initializing)
			return;
		
		initializing = true;
		
		// see who's triggering API startup
		/*try {
			throw new NumberFormatException();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		executeStartups();
		
		servers = new ArrayList();
		activeBundles = new ArrayList();
		loadRuntimesList();
		loadServersList();
		
		pcl = new Preferences.IPropertyChangeListener() {
			public void propertyChange(Preferences.PropertyChangeEvent event) {
				if (ignorePreferenceChanges)
					return;
				String property = event.getProperty();
				if (property.equals("runtimes")) {
					loadRuntimesList();
					saveRuntimesList();
				}
			}
		};
		
		ServerPlugin.getInstance().getPluginPreferences().addPropertyChangeListener(pcl);
		
		resolveServers();
		
		// keep track of future changes to the file system
		resourceChangeListener = new ServerResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE);
		
		Trace.trace(Trace.FINER, "Loading workspace servers and server configurations");
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (projects != null) {
			int size = projects.length;
			for (int i = 0; i < size; i++) {
				if (ServerPlugin.getProjectProperties(projects[i]).isServerProject())
					loadFromProject(projects[i]);
			}
		}
		
		addServerLifecycleListener(ServerListener.getInstance());
		
		initialized = true;
	}

	/**
	 * Load all of the servers and server configurations from the given project.
	 */
	protected static void loadFromProject(IProject project) {
		Trace.trace(Trace.FINER, "Initial server resource load for " + project.getName(), null);
		final ResourceManager rm = ResourceManager.getInstance();
		
		try {
			project.accept(new IResourceProxyVisitor() {
				public boolean visit(IResourceProxy proxy) {
					if (proxy.getType() == IResource.FILE &&
						Server.FILE_EXTENSION.equals(getFileExtension(proxy.getName()))) {
							IFile file = (IFile) proxy.requestResource();
							try {
								rm.handleNewFile(file, null);
							} catch (Exception e) {
								Trace.trace(Trace.SEVERE, "Error during initial server resource load", e);
							}
							return false;
						}
					return true;
				}
			}, 0);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load server project " + project.getName(), e);
		}
	}
		
	protected static String getFileExtension(String name) {
		int index = name.lastIndexOf('.');
		if (index == -1)
			return null;
		if (index == (name.length() - 1))
			return ""; //$NON-NLS-1$
		return name.substring(index + 1);
	}

	public static ResourceManager getInstance() {
		if (instance == null)
			new ResourceManager();

		return instance;
	}

	public static void shutdown() {
		if (instance == null)
			return;
		
		try {
			instance.shutdownImpl();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error during shutdown", e);
		}
	}

	protected boolean isActiveBundle(String bundleId) {
		return activeBundles.contains(bundleId);
	}

	protected void shutdownBundle(String id) {
		if (!initialized)
			return;
		
		// dispose servers
		Iterator iterator = servers.iterator();
		while (iterator.hasNext()) {
			Server server = (Server) iterator.next();
			try {
				ServerType serverType = (ServerType) server.getServerType();
				if (serverType != null && id.equals(serverType.getNamespace())) {
					//server.stop(true);
					server.dispose();
				}
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Error disposing server", e);
			}
		}
		
		// dispose runtimes
		iterator = runtimes.iterator();
		while (iterator.hasNext()) {
			Runtime runtime = (Runtime) iterator.next();
			try {
				RuntimeType runtimeType = (RuntimeType) runtime.getRuntimeType();
				if (id.equals(runtimeType.getNamespace())) {
					runtime.dispose();
				}
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Error disposing server", e);
			}
		}
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// ignore
		}
	}
	
	protected void shutdownImpl() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace != null)
			workspace.removeResourceChangeListener(resourceChangeListener);
		
		ServerPlugin.getInstance().getPluginPreferences().removePropertyChangeListener(pcl);
		
		removeServerLifecycleListener(ServerListener.getInstance());
	}

	/*
	 * 
	 */
	public void addRuntimeLifecycleListener(IRuntimeLifecycleListener listener) {
		Trace.trace(Trace.LISTENERS, "Adding server resource listener " + listener + " to " + this);
	
		if (runtimeListeners == null)
			runtimeListeners = new ArrayList(3);
		runtimeListeners.add(listener);
	}
	
	/*
	 *
	 */
	public void removeRuntimeLifecycleListener(IRuntimeLifecycleListener listener) {
		Trace.trace(Trace.LISTENERS, "Removing server resource listener " + listener + " from " + this);
	
		if (runtimeListeners != null)
			runtimeListeners.remove(listener);
	}
	
	/*
	 * 
	 */
	public void addServerLifecycleListener(IServerLifecycleListener listener) {
		Trace.trace(Trace.LISTENERS, "Adding server resource listener " + listener + " to " + this);
	
		if (serverListeners == null)
			serverListeners = new ArrayList(3);
		serverListeners.add(listener);
	}
	
	/*
	 *
	 */
	public void removeServerLifecycleListener(IServerLifecycleListener listener) {
		Trace.trace(Trace.LISTENERS, "Removing server resource listener " + listener + " from " + this);
	
		if (serverListeners != null)
			serverListeners.remove(listener);
	}
	
	/**
	 * Deregister an existing runtime.
	 *
	 * @param runtime
	 */
	protected void deregisterRuntime(IRuntime runtime) {
		if (runtime == null)
			return;
		
		if (!initialized)
			init();
		
		Trace.trace(Trace.RESOURCES, "Deregistering runtime: " + runtime.getName());
		
		runtimes.remove(runtime);
		fireRuntimeEvent(runtime, EVENT_REMOVED);
		((Runtime)runtime).dispose();
	}

	/**
	 * Deregister an existing server resource.
	 *
	 * @param server
	 */
	protected void deregisterServer(IServer server) {
		if (server == null)
			return;
		
		if (!initialized)
			init();
		
		Trace.trace(Trace.RESOURCES, "Deregistering server: " + server.getName());
		
		((Server) server).deleteMetadata();
		
		servers.remove(server);
		fireServerEvent(server, EVENT_REMOVED);
		((Server)server).dispose();
	}

	/**
	 * Fire a runtime event.
	 */
	private void fireRuntimeEvent(final IRuntime runtime, byte b) {
		Trace.trace(Trace.LISTENERS, "->- Firing runtime event: " + runtime.getName() + " ->-");
		
		if (runtimeListeners == null || runtimeListeners.isEmpty())
			return;
	
		int size = runtimeListeners.size();
		IRuntimeLifecycleListener[] srl = new IRuntimeLifecycleListener[size];
		runtimeListeners.toArray(srl);
	
		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.LISTENERS, "  Firing runtime event to " + srl[i]);
			try {
				if (b == EVENT_ADDED)
					srl[i].runtimeAdded(runtime);
				else if (b == EVENT_CHANGED)
					srl[i].runtimeChanged(runtime);
				else
					srl[i].runtimeRemoved(runtime);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing runtime event to " + srl[i], e);
			}
		}
		Trace.trace(Trace.LISTENERS, "-<- Done firing runtime event -<-");
	}

	/**
	 * Fire a server event.
	 */
	private void fireServerEvent(final IServer server, byte b) {
		Trace.trace(Trace.LISTENERS, "->- Firing server event: " + server.getName() + " ->-");
		
		if (serverListeners == null || serverListeners.isEmpty())
			return;
	
		int size = serverListeners.size();
		IServerLifecycleListener[] srl = new IServerLifecycleListener[size];
		serverListeners.toArray(srl);
	
		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.LISTENERS, "  Firing server event to " + srl[i]);
			try {
				if (b == EVENT_ADDED)
					srl[i].serverAdded(server);
				else if (b == EVENT_CHANGED)
					srl[i].serverChanged(server);
				else
					srl[i].serverRemoved(server);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing server event to " + srl[i], e);
			}
		}
		Trace.trace(Trace.LISTENERS, "-<- Done firing server event -<-");
	}	

	protected void saveRuntimesList() {
		try {
			ignorePreferenceChanges = true;
			XMLMemento memento = XMLMemento.createWriteRoot("runtimes");
			
			Iterator iterator = runtimes.iterator();
			while (iterator.hasNext()) {
				Runtime runtime = (Runtime) iterator.next();
				
				IMemento child = memento.createChild("runtime");
				runtime.save(child);
			}
			
			String xmlString = memento.saveToString();
			Preferences prefs = ServerPlugin.getInstance().getPluginPreferences();
			prefs.setValue("runtimes", xmlString);
			ServerPlugin.getInstance().savePluginPreferences();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save runtimes", e);
		}
		ignorePreferenceChanges = false;
	}

	protected void saveServersList() {
		String filename = ServerPlugin.getInstance().getStateLocation().append(SERVER_DATA_FILE).toOSString();
		
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("servers");
			
			Iterator iterator = servers.iterator();
			while (iterator.hasNext()) {
				Server server = (Server) iterator.next();
				
				IMemento child = memento.createChild("server");
				server.save(child);
			}
			
			memento.saveToFile(filename);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save servers", e);
		}
	}

	protected void loadRuntimesList() {
		Trace.trace(Trace.FINEST, "Loading runtime info");
		Preferences prefs = ServerPlugin.getInstance().getPluginPreferences();
		String xmlString = prefs.getString("runtimes");
		
		runtimes = new ArrayList();
		if (xmlString != null && xmlString.length() > 0) {
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
				IMemento memento = XMLMemento.loadMemento(in);
				
				IMemento[] children = memento.getChildren("runtime");
				int size = children.length;
				
				for (int i = 0; i < size; i++) {
					Runtime runtime = new Runtime(null);
					runtime.loadFromMemento(children[i], null);
					runtimes.add(runtime);
				}
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Could not load runtimes: " + e.getMessage());
			}
		}
	}

	protected void loadServersList() {
		Trace.trace(Trace.FINEST, "Loading server info");
		String filename = ServerPlugin.getInstance().getStateLocation().append(SERVER_DATA_FILE).toOSString();
		
		try {
			IMemento memento = XMLMemento.loadMemento(filename);
			
			IMemento[] children = memento.getChildren("server");
			int size = children.length;
			
			for (int i = 0; i < size; i++) {
				Server server = new Server(null);
				server.loadFromMemento(children[i], null);
				servers.add(server);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load servers: " + e.getMessage());
		}
		
		if (ServerPreferences.getInstance().isSyncOnStartup()) {
			Iterator iterator = servers.iterator();
			while (iterator.hasNext()) {
				IServer server = (IServer) iterator.next();
				UpdateServerJob job = new UpdateServerJob(server);
				job.schedule();
			}
		}
	}

	protected void addRuntime(IRuntime runtime) {
		if (runtime == null)
			return;
		
		if (!initialized)
			init();
		
		if (!runtimes.contains(runtime))
			registerRuntime(runtime);
		else
			fireRuntimeEvent(runtime, EVENT_CHANGED);
		saveRuntimesList();
		resolveServers();
	}

	protected void removeRuntime(IRuntime runtime) {
		if (!initialized)
			init();
		
		if (runtimes.contains(runtime)) {
			deregisterRuntime(runtime);
			saveRuntimesList();
			resolveServers();
		}
	}

	protected void addServer(IServer server) {
		if (server == null)
			return;
		
		if (!initialized)
			init();
		
		if (!servers.contains(server))
			registerServer(server);
		else
			fireServerEvent(server, EVENT_CHANGED);
		saveServersList();
		resolveServers();
	}

	protected void removeServer(IServer server) {
		if (!initialized)
			init();
		
		if (servers.contains(server)) {
			deregisterServer(server);
			saveServersList();
			resolveServers();
		}
	}

	/**
	 * Returns an array of all runtimes.
	 *
	 * @return an array of runtimes
	 */
	public IRuntime[] getRuntimes() {
		if (!initialized)
			init();
		
		List list = new ArrayList(runtimes);
		
		IRuntime[] r = new IRuntime[list.size()];
		list.toArray(r);
		return r;
	}

	/**
	 * Returns the runtime with the given id.
	 * 
	 * @param id a runtime id
	 * @return IRuntime
	 */
	public IRuntime getRuntime(String id) {
		if (id == null)
			throw new IllegalArgumentException();
		
		if (!initialized)
			init();
		
		Iterator iterator = runtimes.iterator();
		while (iterator.hasNext()) {
			IRuntime runtime = (IRuntime) iterator.next();
			if (runtime.getId().equals(id))
				return runtime;
		}
		return null;
	}

	public void resolveRuntimes() {
		if (!initialized)
			init();
		
		Iterator iterator = runtimes.iterator();
		while (iterator.hasNext()) {
			Runtime runtime = (Runtime) iterator.next();
			runtime.resolve();
		}
	}

	public void resolveServers() {
		if (!initialized)
			init();
		
		Iterator iterator = servers.iterator();
		while (iterator.hasNext()) {
			Server server = (Server) iterator.next();
			server.resolve();
		}
	}

	/**
	 * Returns an array containing all servers.
	 *
	 * @return an array containing all servers
	 */
	public IServer[] getServers() {
		if (!initialized)
			init();
		
		IServer[] servers2 = new IServer[servers.size()];
		servers.toArray(servers2);
		
		return servers2;
	}

	/**
	 * Returns the server with the given id.
	 * 
	 * @param id a server id
	 * @return a server
	 */
	public IServer getServer(String id) {
		if (!initialized)
			init();
		
		if (id == null)
			throw new IllegalArgumentException();
	
		Iterator iterator = servers.iterator();
		while (iterator.hasNext()) {
			Server server = (Server) iterator.next();
			if (id.equals(server.getId()))
				return server;
		}
		return null;
	}

	/**
	 * Returns true if the resource change was handled.
	 *
	 * @param delta org.eclipse.core.resources.IResourceDelta
	 * @return boolean
	 */
	protected boolean handleResourceDelta(IResourceDelta delta) {
		int kind = delta.getKind();
		int flags = delta.getFlags();
		IResource resource2 = delta.getResource();
		
		// ignore markers
		if (kind == IResourceDelta.CHANGED && (flags & IResourceDelta.MARKERS) != 0)
			return false;
		
		Trace.trace(Trace.RESOURCES, "Resource changed: " + resource2 + " " + kind);
		
		if (resource2 instanceof IFile) {
			IFile file = (IFile) resource2;
			if (Server.FILE_EXTENSION.equals(file.getFileExtension())) {
				IProgressMonitor monitor = null;
				if ((flags & IResourceDelta.MOVED_FROM) != 0 || (flags & IResourceDelta.MOVED_TO) != 0)
					handleMovedFile(file, delta, monitor);
				else if (kind == IResourceDelta.ADDED)
					handleNewFile(file, monitor);
				else if (kind == IResourceDelta.REMOVED)
					handleRemovedFile(file);
				else
					handleChangedFile(file, monitor);
				if (monitor != null)
					monitor.done();
			}
			return false;
		}
		IFolder folder = (IFolder) resource2;
		Iterator iterator = servers.iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			if (server.getServerType() != null && server.getServerType().hasServerConfiguration() && folder.equals(server.getServerConfiguration())
					&& server.getAdapter(ServerDelegate.class) != null) {
				try {
					((Server)server).getDelegate(null).configurationChanged();
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Server failed on configuration change");
				}
			}
		}
		return true;
	}

	protected IServer loadServer(IFile file, IProgressMonitor monitor) throws CoreException {
		Server server = new Server(file);
		server.loadFromFile(monitor);
		return server;
	}

	/**
	 * Tries to load a new server resource from the given resource.
	 * Returns true if the load and register were successful.
	 *
	 * @param file
	 * @param monitor
	 * @return boolean
	 */
	protected boolean handleNewFile(IFile file, IProgressMonitor monitor) {
		Trace.trace(Trace.RESOURCES, "handleNewFile: " + file);
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask("", 2000);
		
		// try loading a server
		if (file.getFileExtension().equals(Server.FILE_EXTENSION)) {
			try {
				IServer server = loadServer(file, ProgressUtil.getSubMonitorFor(monitor, 1000));
				if (server != null) {
					if (getServer(server.getId()) == null)
						registerServer(server);
					monitor.done();
					return true;
				}
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error loading server", e);
			}
		}
		
		monitor.done();
		return false;
	}

	/**
	 * Tries to load a new server resource from the given resource.
	 * Returns true if the load and register were successful.
	 *
	 * @param file
	 * @param monitor
	 * @return boolean
	 */
	protected boolean handleMovedFile(IFile file, IResourceDelta delta, IProgressMonitor monitor) {
		Trace.trace(Trace.RESOURCES, "handleMovedFile: " + file);
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask("", 2000);
		
		IPath fromPath = delta.getMovedFromPath();
		if (fromPath != null) {
			IFile fromFile = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPath);
			if (ServerPlugin.getProjectProperties(fromFile.getProject()).isServerProject()) {
				Server server = (Server) findServer(fromFile);
				if (server != null)
					server.file = file;
			} else {
				handleNewFile(file, monitor);
			}
		} else {
			IPath toPath = delta.getMovedToPath();
			IFile toFile = ResourcesPlugin.getWorkspace().getRoot().getFile(toPath);
			if (ServerPlugin.getProjectProperties(toFile.getProject()).isServerProject()) {
				Server server = (Server) findServer(file);
				if (server != null)
					server.file = toFile;
			} else {
				handleRemovedFile(file);
			}
		}
		
		monitor.done();
		return false;
	}

	/**
	 * Returns the server that came from the given file, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * servers ({@link #getServers()}) for the one with a matching
	 * location ({@link Server#getFile()}). The file may not be null.
	 *
	 * @param file a server file
	 * @return the server instance, or <code>null</code> if 
	 *    there is no server associated with the given file
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
	 * Tries to handle a resource change. Returns true if the reload
	 * was successful.
	 *
	 * @param file a file
	 * @param monitor
	 * @return boolean
	 */
	protected boolean handleChangedFile(IFile file, IProgressMonitor monitor) {
		Trace.trace(Trace.RESOURCES, "handleChangedFile: " + file);
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask("", 1000);
		boolean found = false;
	
		IServer server = findServer(file);
		if (server != null) {
			found = true;
			try {
				Trace.trace(Trace.RESOURCES, "Reloading server: " + server);
				((Server) server).loadFromFile(monitor);
				fireServerEvent(server, EVENT_CHANGED);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error reloading server " + server.getName() + " from " + file + ": " + e.getMessage());
				deregisterServer(server);
			}
		} else
			Trace.trace(Trace.RESOURCES, "No server found at: " + file);
		
		monitor.done();
		return found;
	}

	/**
	 * Tries to remove a current resource. Returns true if the
	 * deregistering was successful.
	 *
	 * @param file a file
	 * @return boolean
	 */
	protected boolean handleRemovedFile(IFile file) {
		Trace.trace(Trace.RESOURCES, "handleRemovedFile: " + file);
		
		IServer server = findServer(file);
		if (server != null) {
			deregisterServer(server);
			return true;
		}
		
		Trace.trace(Trace.RESOURCES, "No server found at: " + file);
		return false;
	}

	/**
	 * A project has changed. If this is an add or remove, check
	 * to see if it is part of a current server configuration.
	 *
	 * @param delta org.eclipse.core.resources.IResourceDelta
	 */
	protected void publishHandleProjectChange(IResourceDelta delta) {
		Trace.trace(Trace.FINEST, "> publishHandleProjectChange " + delta.getResource());
		IProject project = (IProject) delta.getResource();
		
		if (project == null)
			return;
		
		if (!deltaContainsChangedFiles(delta))
			return;
		
		// process module changes
		ProjectModuleFactoryDelegate.handleGlobalProjectChange(project, delta);
		
		IModule[] modules = ServerUtil.getModules(project);
		if (modules == null)
			return;
		
		Trace.trace(Trace.FINEST, "- publishHandleProjectChange");
		
		IServer[] servers2 = getServers();
		int size = modules.length;
		int size2 = servers2.length;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size2; j++) {
				if (servers2[j].getAdapter(ServerDelegate.class) != null)
					((Server) servers2[j]).handleModuleProjectChange(modules[i]);
			}
		}
		Trace.trace(Trace.FINEST, "< publishHandleProjectChange");
	}

	/**
	 * Returns <code>true</code> if at least one file in the delta is changed,
	 * and <code>false</code> otherwise.
	 * 
	 * @param delta a resource delta
	 * @return <code>true</code> if at least one file in the delta is changed,
	 *    and <code>false</code> otherwise
	 */
	public static boolean deltaContainsChangedFiles(IResourceDelta delta) {
		final boolean[] b = new boolean[1];
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta2) throws CoreException {
					if (b[0])
						return false;
					//Trace.trace(Trace.FINEST, delta2.getResource() + "  " + delta2.getKind() + " " + delta2.getFlags());
					if (delta2.getKind() == IResourceDelta.NO_CHANGE)
						return false;
					if (delta2.getResource() instanceof IFile) {
						if (delta2.getKind() == IResourceDelta.CHANGED
							&& (delta2.getFlags() & IResourceDelta.CONTENT) == 0
							&& (delta2.getFlags() & IResourceDelta.REPLACED) == 0
							&& (delta2.getFlags() & IResourceDelta.SYNC) == 0)
							return true;
						//if (delta2.getKind() == IResourceDelta.CHANGED) { // && delta2.getAffectedChildren().length == 0) {
						b[0] = true;
						return false;
							//return true;
						//}
					}
					return true;
				}
			});
		} catch (Exception e) {
			// ignore
		}
		//Trace.trace(Trace.FINEST, "Delta contains change: " + t.b);
		return b[0];
	}

	/**
	 * Registers a new runtime.
	 *
	 * @param runtime org.eclipse.wst.server.core.IRuntime
	 */
	protected void registerRuntime(IRuntime runtime) {
		if (runtime == null)
			return;
		
		if (!initialized)
			init();
		
		Trace.trace(Trace.RESOURCES, "Registering runtime: " + runtime.getName());
		
		runtimes.add(runtime);
		fireRuntimeEvent(runtime, EVENT_ADDED);
		
		RuntimeType runtimeType = (RuntimeType) runtime.getRuntimeType();
		String bundleId = runtimeType.getNamespace();
		if (!activeBundles.contains(bundleId))
			activeBundles.add(bundleId);
	}

	/**
	 * Registers a new server.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	protected void registerServer(IServer server) {
		if (server == null)
			return;
		
		if (!initialized)
			init();
		
		Trace.trace(Trace.RESOURCES, "Registering server: " + server.getName());
		
		servers.add(server);
		fireServerEvent(server, EVENT_ADDED);
		
		ServerType serverType = (ServerType) server.getServerType();
		String bundleId = serverType.getNamespace();
		if (!activeBundles.contains(bundleId))
			activeBundles.add(bundleId);
	}

	protected void fireModuleServerEvent(ModuleFactoryEvent[] factoryEvents, ModuleEvent[] events) {
		// do nothing
	}

	public String toString() {
		return "Server resource manager";
	}
}