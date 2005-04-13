/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
import org.eclipse.wst.server.core.model.*;
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

	// currently active runtimes, servers, and server configurations
	protected List runtimes;
	protected List servers;
	protected List configurations;
	protected IRuntime defaultRuntime;

	// lifecycle listeners
	protected transient List runtimeListeners;
	protected transient List serverListeners;
	protected transient List serverConfigurationListeners;

	// resource change listeners
	private IResourceChangeListener modelResourceChangeListener;
	private IResourceChangeListener publishResourceChangeListener;
	private Preferences.IPropertyChangeListener pcl;
	protected boolean ignorePreferenceChanges = false;

	/**
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
	public class ServerModelResourceChangeListener implements IResourceChangeListener {
		/**
		 * Create a new ServerModelResourceChangeListener.
		 */
		public ServerModelResourceChangeListener() {
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
	
			Trace.trace(Trace.RESOURCES, "->- ServerModelResourceManager responding to resource change: " + event.getType() + " ->-");
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
	
			Trace.trace(Trace.RESOURCES, "-<- Done ServerModelResourceManager responding to resource change -<-");
		}
	
		/**
		 * React to a change within a possible server project.
		 *
		 * @param delta org.eclipse.core.resources.IResourceDelta
		 */
		protected void projectChanged(IProject project, IResourceDelta delta) {
			if (!((ProjectProperties)ServerCore.getProjectProperties(project)).isServerProject()) {
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
	 * Publish resource listener
	 */
	public class PublishResourceChangeListener implements IResourceChangeListener {
		/**
		 * Create a new PublishResourceChangeListener.
		 */
		public PublishResourceChangeListener() {
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
		
			Trace.trace(Trace.FINEST, "->- PublishResourceManager responding to resource change: " + event.getType() + " ->-");
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
			Trace.trace(Trace.FINEST, "-<- Done PublishResourceManager responding to resource change -<-");
		}
	}
	
	protected List moduleServerEventHandlers;
	protected List moduleServerEventHandlerIndexes;

	/**
	 * Cannot directly create a ResourceManager. Use
	 * ServersCore.getResourceManager().
	 */
	private ResourceManager() {
		super();
		instance = this;
		
		init();
	}

	protected void init() {
		servers = new ArrayList();
		configurations = new ArrayList();
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
		modelResourceChangeListener = new ServerModelResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(modelResourceChangeListener, IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE);
	
		// add listener for future changes
		publishResourceChangeListener = new PublishResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(publishResourceChangeListener, IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_DELETE);
	
		/*configurationListener = new IServerConfigurationListener() {
			public void childProjectChange(IServerConfiguration configuration) {
				handleConfigurationChildProjectsChange(configuration);
			}
		};*/
		
		Trace.trace(Trace.FINER, "Loading workspace servers and server configurations");
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (projects != null) {
			int size = projects.length;
			for (int i = 0; i < size; i++) {
				if (((ProjectProperties)ServerCore.getProjectProperties(projects[i])).isServerProject())
					loadFromProject(projects[i]);
			}
		}
		
		addServerLifecycleListener(ServerListener.getInstance());
	}
	
	/**
	 * Load all of the servers and server configurations from the given project.
	 */
	protected static void loadFromProject(IProject project) {
		Trace.trace(Trace.FINER, "Initial server resource load for " + project.getName(), null);
		final ResourceManager rm = ResourceManager.getInstance();
	
		try {
			project.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) {
					try {
						if (resource instanceof IFile) {
							IFile file = (IFile) resource;
							rm.handleNewFile(file, new NullProgressMonitor());
							return false;
						}
						return true;
						//return !rm.handleNewServerResource(resource, new NullProgressMonitor());
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE, "Error during initial server resource load", e);
					}
					return true;
				}
			});
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load server project " + project.getName(), e);
		}
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
	
	protected void shutdownImpl() {
		// stop all running servers
		// REMOVING FEATURE - can't be supported since we can't reload downstream plugins
		// during shutdown. Individual downstream plugins should contain their own similar
		// code to stop the servers.
		/*Iterator iterator = getServers().iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			try {
				if (server.getServerState() != IServer.STATE_STOPPED) {
					ServerDelegate delegate = server.getDelegate();
					if (delegate instanceof IStartableServer && ((IStartableServer)delegate).isTerminateOnShutdown())
						((IStartableServer) delegate).terminate();
				}
			} catch (Exception e) { }
		}*/

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace != null) {
			workspace.removeResourceChangeListener(modelResourceChangeListener);
			workspace.removeResourceChangeListener(publishResourceChangeListener);
		}
		
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

		Trace.trace(Trace.RESOURCES, "Deregistering runtime: " + runtime.getName());

		((Runtime)runtime).dispose();
		fireRuntimeEvent(runtime, EVENT_REMOVED);
		runtimes.remove(runtime);
	}

	/**
	 * Deregister an existing server resource.
	 *
	 * @param server
	 */
	protected void deregisterServer(IServer server) {
		if (server == null)
			return;

		Trace.trace(Trace.RESOURCES, "Deregistering server: " + server.getName());
		
		((Server) server).deleteLaunchConfigurations();
		ServerPlugin.getInstance().removeTempDirectory(server.getId());

		((Server)server).dispose();
		fireServerEvent(server, EVENT_REMOVED);
		servers.remove(server);
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
			
			if (defaultRuntime != null) {
				int ind = runtimes.indexOf(defaultRuntime);
				if (ind >= 0)
					memento.putString("default", ind + "");
			}

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
					runtime.loadFromMemento(children[i], new NullProgressMonitor());
					runtimes.add(runtime);
				}
				
				String s = memento.getString("default");
				try {
					int ind = Integer.parseInt(s);
					defaultRuntime = (IRuntime) runtimes.get(ind);
				} catch (Exception ex) {
					// ignore
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
				server.loadFromMemento(children[i], new NullProgressMonitor());
				servers.add(server);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load servers: " + e.getMessage());
		}
	}
	
	protected void addRuntime(IRuntime runtime) {
		if (runtime == null)
			return;
		if (!runtimes.contains(runtime))
			registerRuntime(runtime);
		else
			fireRuntimeEvent(runtime, EVENT_CHANGED);
		saveRuntimesList();
		resolveServers();
		RuntimeWorkingCopy.rebuildRuntime(runtime, true);
	}

	protected void removeRuntime(IRuntime runtime) {
		if (runtimes.contains(runtime)) {
			deregisterRuntime(runtime);
			saveRuntimesList();
			resolveServers();
			RuntimeWorkingCopy.rebuildRuntime(runtime, false);
		}
	}

	protected void addServer(IServer server) {
		if (!servers.contains(server))
			registerServer(server);
		else
			fireServerEvent(server, EVENT_CHANGED);
		saveServersList();
		resolveServers();
	}

	protected void removeServer(IServer server) {
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
		List list = new ArrayList(runtimes);
		
		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				IRuntime a = (IRuntime) list.get(i);
				IRuntime b = (IRuntime) list.get(j);
				if (a.getRuntimeType() != null && b.getRuntimeType() != null &&
						((RuntimeType)a.getRuntimeType()).getOrder() < ((RuntimeType)b.getRuntimeType()).getOrder()) {
					Object temp = a;
					list.set(i, b);
					list.set(j, temp);
				}
			}
		}
		
		if (defaultRuntime != null) {
			list.remove(defaultRuntime);
			list.add(0, defaultRuntime);
		}
		
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

		Iterator iterator = runtimes.iterator();
		while (iterator.hasNext()) {
			IRuntime runtime = (IRuntime) iterator.next();
			if (runtime.getId().equals(id))
				return runtime;
		}
		return null;
	}
	
	/**
	 * Returns the default runtime. Test API - do not use.
	 *
	 * @return java.util.List
	 */
	public IRuntime getDefaultRuntime() {
		return defaultRuntime;
	}
	
	/**
	 * Sets the default runtime. Test API - do not use.
	 * 
	 * @param runtime a runtime
	 */
	public void setDefaultRuntime(IRuntime runtime) {
		defaultRuntime = runtime;
		saveRuntimesList();
	}

	protected void resolveServers() {
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
		IServer[] servers2 = new IServer[servers.size()];
		servers.toArray(servers2);
		
		Arrays.sort(servers2, new Comparator() {
			public int compare(Object o1, Object o2) {
				IServer a = (IServer) o1;
				IServer b = (IServer) o2;
				return a.getName().compareToIgnoreCase(b.getName());
			}
		});
		
		return servers2;
	}

	/**
	 * Returns the server with the given id.
	 * 
	 * @param id a server id
	 * @return a server
	 */
	public IServer getServer(String id) {
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
		IResource resource2 = delta.getResource();
	
		// ignore markers
		if (kind == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.MARKERS) != 0)
			return false;
	
		Trace.trace(Trace.RESOURCES, "Resource changed: " + resource2 + " " + kind);
		
		if (resource2 instanceof IFile) {
			IFile file = (IFile) resource2;
			IProgressMonitor monitor = new NullProgressMonitor();
			if (kind == IResourceDelta.ADDED) {
				handleNewFile(file, monitor);
			} else if (kind == IResourceDelta.REMOVED) {
				handleRemovedFile(file);
			} else
				handleChangedFile(file, monitor);
			monitor.done();
			return false;
		}
		IFolder folder = (IFolder) resource2;
		Iterator iterator = servers.iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			if (server.getServerType().hasServerConfiguration() && folder.equals(server.getServerConfiguration())
					&& server.isDelegateLoaded()) {
				try {
					((Server)server).getDelegate().configurationChanged();
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Server failed on configuration change");
				}
			}
		}
		return true;
	
		/*IProgressMonitor monitor = new NullProgressMonitor();
		List list = getResourceParentList(resource2);
		monitor.beginTask("", list.size() * 1000);
	
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			IResource resource = (IResource) iterator.next();
			if (!visited.contains(resource.getFullPath())) {
				visited.add(resource.getFullPath());
				if (kind == IResourceDelta.REMOVED) {
					boolean b = handleRemovedFile(resource);
					if (b) {
						if (resource instanceof IContainer)
							removeServerResourcesBelow((IContainer) resource);
						return false;
					} else
						return true;
				} else if (kind == IResourceDelta.ADDED) {
					return !handleNewServerResource(resource, monitor);
				} else {
					boolean b = handleChangedServerResource(resource, monitor);
					if (!b) {
						handleRemovedFile(resource);
					}
					return true;
				}
			}
		}

		monitor.done();
		Trace.trace(Trace.RESOURCES, "Ignored resource change: " + resource2);
		return true;*/
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
		}

		Trace.trace(Trace.RESOURCES, "No server resource found at: " + file);
	
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
		Trace.trace(Trace.RESOURCES, "handleRemovedServerResource: " + file);
	
		IServer server = findServer(file);
		if (server != null) {
			deregisterServer(server);
			return true;
		}

		Trace.trace(Trace.RESOURCES, "No server resource found at: " + file);
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
		
		if (isDeltaOnlyMarkers(delta))
			return;

		final IModule[] modules = ServerUtil.getModules(project);
		if (modules == null)
			return;
		
		Trace.trace(Trace.FINEST, "- publishHandleProjectChange");

		if (modules != null) {
			int size2 = modules.length;
			for (int j = 0; j < size2; j++) {
				IServer[] servers2 = getServers();
				if (servers2 != null) {
					int size = servers2.length;
					for (int i = 0; i < size; i++) {
					if (servers2[i].isDelegateLoaded())
						((Server) servers2[i]).handleModuleProjectChange(modules[j]);
					}
				}
			}
		}
		Trace.trace(Trace.FINEST, "< publishHandleProjectChange");
	}
	
	protected boolean isDeltaOnlyMarkers(IResourceDelta delta) {
		class Temp {
			boolean b = true;
		}
		final Temp t = new Temp();
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta2) throws CoreException {
					if (!t.b)
						return false;
					int flags = delta2.getFlags();
					if (flags != 0 && flags != IResourceDelta.MARKERS) {
						t.b = false;
						return false;
					}
					return true;
				}
			});
		} catch (Exception e) {
			// ignore
		}
		return t.b;
	}
	
	/**
	 * Registers a new runtime.
	 *
	 * @param runtime org.eclipse.wst.server.core.IRuntime
	 */
	protected void registerRuntime(IRuntime runtime) {
		if (runtime == null)
			return;
	
		Trace.trace(Trace.RESOURCES, "Registering runtime: " + runtime.getName());
	
		runtimes.add(runtime);
		fireRuntimeEvent(runtime, EVENT_ADDED);
	}
	
	/**
	 * Registers a new server.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	protected void registerServer(IServer server) {
		if (server == null)
			return;
	
		Trace.trace(Trace.RESOURCES, "Registering server: " + server.getName());
	
		servers.add(server);
		fireServerEvent(server, EVENT_ADDED);
	}

	protected void fireModuleServerEvent(ModuleFactoryEvent[] factoryEvents, ModuleEvent[] events) {
		// do nothing
	}
}