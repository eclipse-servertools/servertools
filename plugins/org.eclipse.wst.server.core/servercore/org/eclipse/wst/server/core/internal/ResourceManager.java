/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
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
	private static final String SERVER_CONFIGURATION_DATA_FILE = "configurations.xml";
	
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
	
	// module factory listener
	private IModuleFactoryListener moduleFactoryListener;
	protected List moduleFactoryEvents = new ArrayList(5);
	
	// module listener
	protected IModuleListener moduleListener;
	protected List moduleEvents = new ArrayList(5);
	
	// module events listeners
	protected transient List moduleEventListeners;

	protected static List serverProjects = new ArrayList();

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
			if (serverProjects.contains(project)) {
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
	
	public class ModuleFactoryListener implements IModuleFactoryListener {
		public void moduleFactoryChanged(IModuleFactoryEvent event) {
			Trace.trace(Trace.FINEST, "Module factory changed: " + event.getFactoryId());
			moduleFactoryEvents.add(event);

			// add new listeners
			IModule[] modules = event.getAddedModules();
			if (modules != null) {
				int size = modules.length;
				for (int i = 0; i < size; i++) {
					Trace.trace(Trace.FINEST, "Adding module listener to: " + modules[i]);
					modules[i].addModuleListener(moduleListener);
				}
			}
			
			// remove old listeners
			modules = event.getRemovedModules();
			if (modules != null) {
				int size = modules.length;
				for (int i = 0; i < size; i++) {
					Trace.trace(Trace.FINEST, "Removing module listener from: " + modules[i]);
					modules[i].removeModuleListener(moduleListener);
				}
			}
		}
	}

	public class ModuleListener implements IModuleListener {
		public void moduleChanged(IModuleEvent event) {
			Trace.trace(Trace.FINEST, "Module changed: " + event);
			if (!moduleEvents.contains(event))
				moduleEvents.add(event);
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
		loadServerConfigurationsList();
		
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
				if (ServerCore.getProjectProperties(projects[i]).isServerProject()) {
					serverProjects.add(projects[i]);
					loadFromProject(projects[i]);
				}
			}
		}
		
		moduleFactoryListener = new ModuleFactoryListener();
		moduleListener = new ModuleListener();
		
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
	
	/*
	 * 
	 */
	public void addServerConfigurationLifecycleListener(IServerConfigurationLifecycleListener listener) {
		Trace.trace(Trace.LISTENERS, "Adding server configuration listener " + listener + " to " + this);
	
		if (serverConfigurationListeners == null)
			serverConfigurationListeners = new ArrayList(3);
		serverConfigurationListeners.add(listener);
	}
	
	/*
	 *
	 */
	public void removeServerConfigurationLifecycleListener(IServerConfigurationLifecycleListener listener) {
		Trace.trace(Trace.LISTENERS, "Removing server configuration listener " + listener + " from " + this);
	
		if (serverConfigurationListeners != null)
			serverConfigurationListeners.remove(listener);
	}
	
	/**
	 * Deregister an existing runtime.
	 *
	 * @param resource org.eclipse.core.resources.IResource
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
	 * @param resource org.eclipse.core.resources.IResource
	 */
	protected void deregisterServer(IServer server) {
		if (server == null)
			return;

		Trace.trace(Trace.RESOURCES, "Deregistering server: " + server.getName());
		
		((Server) server).deleteLaunchConfigurations();
		ServerPlugin.getInstance().removeTempDirectory(server.getId(), new NullProgressMonitor());

		((Server)server).dispose();
		fireServerEvent(server, EVENT_REMOVED);
		servers.remove(server);
	}

	/**
	 * Deregister an existing server resource.
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 */
	protected void deregisterServerConfiguration(IServerConfiguration configuration) {
		if (configuration == null)
			return;
	
		Trace.trace(Trace.RESOURCES, "Deregistering server configuration: " + configuration.getName());

		((ServerConfiguration)configuration).dispose();
		resolveServers();
		fireServerConfigurationEvent(configuration, EVENT_REMOVED);
		configurations.remove(configuration);
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
	
	/**
	 * Fire a server configuration event.
	 */
	private void fireServerConfigurationEvent(final IServerConfiguration config, byte b) {
		Trace.trace(Trace.LISTENERS, "->- Firing server config event: " + config.getName() + " ->-");
		
		if (serverConfigurationListeners == null || serverConfigurationListeners.isEmpty())
			return;
	
		int size = serverConfigurationListeners.size();
		IServerConfigurationLifecycleListener[] srl = new IServerConfigurationLifecycleListener[size];
		serverListeners.toArray(srl);
	
		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.LISTENERS, "  Firing server config event to " + srl[i]);
			try {
				if (b == EVENT_ADDED)
					srl[i].serverConfigurationAdded(config);
				else if (b == EVENT_CHANGED)
					srl[i].serverConfigurationChanged(config);
				else
					srl[i].serverConfigurationRemoved(config);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing server config event to " + srl[i], e);
			}
		}
		Trace.trace(Trace.LISTENERS, "-<- Done firing server config event -<-");
	}

	/**
	 * Returns an array of all currently active server configurations.
	 *
	 * @return
	 */
	public IServerConfiguration[] getServerConfigurations() {
		List list = new ArrayList(configurations);
		List list2 = ServerPlugin.sortServerResourceList(list);
		
		IServerConfiguration[] sc = new IServerConfiguration[list2.size()];
		list2.toArray(sc);
		return sc;
	}
	
	/**
	 * Returns a list of all servers.
	 *
	 * @return java.util.List
	 */
	public IServerConfiguration getServerConfiguration(String id) {
		if (id == null)
			throw new IllegalArgumentException();
	
		Iterator iterator = configurations.iterator();
		while (iterator.hasNext()) {
			IServerConfiguration config = (IServerConfiguration) iterator.next();
			if (id.equals(config.getId()))
				return config;
		}
		return null;
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
	
	protected void saveServerConfigurationsList() {
		String filename = ServerPlugin.getInstance().getStateLocation().append(SERVER_CONFIGURATION_DATA_FILE).toOSString();
		
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("server-configurations");

			Iterator iterator = configurations.iterator();
			while (iterator.hasNext()) {
				ServerConfiguration config = (ServerConfiguration) iterator.next();

				IMemento child = memento.createChild("configuration");
				config.save(child);
			}

			memento.saveToFile(filename);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save server configurations", e);
		}
	}
	
	protected void loadRuntimesList() {
		Trace.trace(Trace.FINEST, "Loading runtime info");
		Preferences prefs = ServerPlugin.getInstance().getPluginPreferences();
		String xmlString = prefs.getString("runtimes");
		
		runtimes = new ArrayList();
		if (xmlString != null && xmlString.length() > 0) {
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes());
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
	
	protected void loadServerConfigurationsList() {
		Trace.trace(Trace.FINEST, "Loading server configuration info");
		String filename = ServerPlugin.getInstance().getStateLocation().append(SERVER_CONFIGURATION_DATA_FILE).toOSString();
		
		try {
			IMemento memento = XMLMemento.loadMemento(filename);
			
			IMemento[] children = memento.getChildren("configuration");
			int size = children.length;
			
			for (int i = 0; i < size; i++) {
				ServerConfiguration config = new ServerConfiguration(null);
				config.loadFromMemento(children[i], new NullProgressMonitor());
				configurations.add(config);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load server configurations: " + e.getMessage());
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

	protected void addServerConfiguration(IServerConfiguration config) {
		if (!configurations.contains(config))
			registerServerConfiguration(config);
		else
			fireServerConfigurationEvent(config, EVENT_CHANGED);
		saveServerConfigurationsList();
		resolveServers();
	}

	protected void removeServerConfiguration(IServerConfiguration config) {
		if (configurations.contains(config)) {
			deregisterServerConfiguration(config);
			saveServerConfigurationsList();
			resolveServers();
		}
	}

	/**
	 * Returns an array of all runtimes.
	 *
	 * @return
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
	 * @return java.util.List
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
	 * Returns an array of all servers.
	 *
	 * @return
	 */
	public IServer[] getServers() {
		List list = new ArrayList(servers);
		List list2 = ServerPlugin.sortServerResourceList(list);
		
		IServer[] s = new IServer[list2.size()];
		list2.toArray(s);
		return s;
	}

	/**
	 * Returns the server with the given id.
	 *
	 * @return
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
	 * Handle a change to the child projects of this configuration.
	 *
	 * @param configuration org.eclipse.wst.server.core.model.IServerConfiguration
	 */
	protected void handleConfigurationChildProjectsChange(IServerConfiguration configuration) {
		/*String configRef = ServerCore.getServerConfigurationRef(configuration);
		if (configRef == null || configRef.length() == 0)
			return;
	
		Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
		while (iterator.hasNext()) {
			IServer2 server = (IServer2) iterator.next();
			if (server.getServerConfiguration().equals(configuration)) {
				ServerControl control = (ServerControl) ServerCore.getServerControl(server);
				control.handleConfigurationChildProjectChange(configuration);
			}
		}*/
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
	
	protected IServerConfiguration loadServerConfiguration(IFile file, IProgressMonitor monitor) throws CoreException {
		ServerConfiguration config = new ServerConfiguration(file);
		config.loadFromFile(monitor);
		return config;
	}
	
	/**
	 * Tries to load a new server resource from the given resource.
	 * Returns true if the load and register were successful.
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 * @return boolean
	 */
	protected boolean handleNewFile(IFile file, IProgressMonitor monitor) {
		Trace.trace(Trace.RESOURCES, "handleNewFile: " + file);
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask("", 2000);
		
		// try loading a server
		if (file.getFileExtension().equals(IServer.FILE_EXTENSION)) {
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
		} else if (file.getFileExtension().equals(IServerConfiguration.FILE_EXTENSION)) {
	
			// try loading a server configuration
			try {
				IServerConfiguration config = loadServerConfiguration(file, ProgressUtil.getSubMonitorFor(monitor, 1000));
				if (config != null) {
					registerServerConfiguration(config);
					monitor.done();
					return true;
				}
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error loading configuration", e);
			}
		}
	
		monitor.done();
		return false;
	}

	/**
	 * Tries to handle a resource change. Returns true if the reload
	 * was successful.
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 * @return boolean
	 */
	protected boolean handleChangedFile(IFile file, IProgressMonitor monitor) {
		Trace.trace(Trace.RESOURCES, "handleChangedFile: " + file);
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask("", 1000);
		boolean found = false;
	
		IServer server = ServerUtil.getServer(file);
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
		
		IServerConfiguration configuration = ServerUtil.getServerConfiguration(file);
		if (configuration != null) {
			found = true;
			try {
				Trace.trace(Trace.RESOURCES, "Reloading configuration: " + configuration);
				((ServerConfiguration) configuration).loadFromFile(monitor);
				fireServerConfigurationEvent(configuration, EVENT_CHANGED);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error reloading configuration " + configuration.getName() + " from " + file + ": " + e.getMessage());
				deregisterServerConfiguration(configuration);
			}

			// TODO find any running servers that contain this configuration
			// notify the servers with this configuration
			/*Iterator iterator = getServers().iterator();
			while (iterator.hasNext()) {
				IServer server2 = (IServer) iterator.next();
				if (server2.getServerConfiguration().equals(configuration))
					server2.updateConfiguration();
			}*/
			fireServerConfigurationEvent(configuration, EVENT_CHANGED);
		}

		Trace.trace(Trace.RESOURCES, "No server resource found at: " + file);
	
		monitor.done();
		return found;
	}

	/**
	 * Tries to remove a current resource. Returns true if the
	 * deregistering was successful.
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 * @return boolean
	 */
	protected boolean handleRemovedFile(IFile file) {
		Trace.trace(Trace.RESOURCES, "handleRemovedServerResource: " + file);
	
		IServer server = ServerUtil.getServer(file);
		if (server != null) {
			deregisterServer(server);
			return true;
		}
		IServerConfiguration config = ServerUtil.getServerConfiguration(file);
		if (config != null) {
			deregisterServerConfiguration(config);
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

		final IModule[] moduleProject = ServerUtil.getModules(project);
		if (moduleProject == null)
			return;
		
		Trace.trace(Trace.FINEST, "- publishHandleProjectChange");

		if (moduleProject != null) {
			int size2 = moduleProject.length;
			for (int j = 0; j < size2; j++) {
				IServer[] servers2 = getServers();
				if (servers2 != null) {
					int size = servers2.length;
					for (int i = 0; i < size; i++) {
					if (servers2[i].isDelegateLoaded())
						((Server) servers2[i]).handleModuleProjectChange(delta, new IModule[] { moduleProject[j] });
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
	 * @param resource org.eclipse.core.resources.IResource
	 * @param element org.eclipse.wst.server.core.model.IServerResource
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
	 * @param resource org.eclipse.core.resources.IResource
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	protected void registerServer(IServer server) {
		if (server == null)
			return;
	
		Trace.trace(Trace.RESOURCES, "Registering server: " + server.getName());
	
		servers.add(server);
		fireServerEvent(server, EVENT_ADDED);
	}

	/**
	 * Registers a new server configuration resource.
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	protected void registerServerConfiguration(IServerConfiguration config) {
		if (config == null)
			return;
	
		Trace.trace(Trace.RESOURCES, "Registering server configuration: " + config.getName());
	
		configurations.add(config);
		resolveServers();
		fireServerConfigurationEvent(config, EVENT_ADDED);
	}

	/**
	 *
	 */
	protected void addModuleFactoryListener(ModuleFactoryDelegate delegate) {
		if (delegate == null)
			return;
	
		Trace.trace(Trace.LISTENERS, "Adding module factory listener to: " + delegate);
		delegate.addModuleFactoryListener(moduleFactoryListener);
		
		IModule[] modules = delegate.getModules();
		if (modules != null) {
			int size = modules.length;
			for (int i = 0; i < size; i++) {
				Trace.trace(Trace.LISTENERS, "Adding module listener to: " + modules[i]);
				modules[i].addModuleListener(moduleListener);
			}
		}
	}

	/**
	 * Adds a new module events listener.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IModuleEventsListener
	 */
	public void addModuleEventsListener(IModuleEventsListener listener) {
		Trace.trace(Trace.LISTENERS, "Adding moduleEvents listener " + listener + " to " + this);
	
		if (moduleEventListeners == null)
			moduleEventListeners = new ArrayList();
		moduleEventListeners.add(listener);
	}
	
	/**
	 * Removes an existing module events listener.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IModuleEventsListener
	 */
	public void removeModuleEventsListener(IModuleEventsListener listener) {
		Trace.trace(Trace.LISTENERS, "Removing moduleEvents listener " + listener + " to " + this);
	
		if (moduleEventListeners != null)
			moduleEventListeners.remove(listener);
	}
	
	/**
	 * Module events have momentarily stopped firing and should be
	 * handled appropriately.
	 */
	public void syncModuleEvents() {
		if (moduleEvents.isEmpty() && moduleFactoryEvents.isEmpty())
			return;

		Trace.trace(Trace.LISTENERS, "->- Firing moduleEvents " + moduleEvents.size() + " " + moduleFactoryEvents.size());
		Iterator iterator = moduleEvents.iterator();
		while (iterator.hasNext()) {
			IModuleEvent event = (IModuleEvent) iterator.next();
			Trace.trace(Trace.LISTENERS, "  1> " + event);
		}
		iterator = moduleFactoryEvents.iterator();
		while (iterator.hasNext()) {
			IModuleFactoryEvent event = (IModuleFactoryEvent) iterator.next();
			Trace.trace(Trace.LISTENERS, "  2> " + event);
		}

		IModuleEvent[] events = new IModuleEvent[moduleEvents.size()];
		moduleEvents.toArray(events);
		
		IModuleFactoryEvent[] factoryEvents = new IModuleFactoryEvent[moduleFactoryEvents.size()];
		moduleFactoryEvents.toArray(factoryEvents);
		
		if (moduleEventListeners != null) {
			iterator = moduleEventListeners.iterator();
			while (iterator.hasNext()) {
				IModuleEventsListener listener = (IModuleEventsListener) iterator.next();
				try {
					Trace.trace(Trace.LISTENERS, "  Firing moduleEvents to " + listener);
					listener.moduleEvents(factoryEvents, events);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "  Error firing moduleEvents to " + listener);
				}
			}
		}
		
		// fire module server events
		fireModuleServerEvent(factoryEvents, events);
		
		// clear cache
		moduleEvents = new ArrayList(5);
		moduleFactoryEvents = new ArrayList(5);
		
		Trace.trace(Trace.LISTENERS, "-<- Firing moduleEvents " + moduleEvents.size() + " " + moduleFactoryEvents.size());
	}

	protected void fireModuleServerEvent(IModuleFactoryEvent[] factoryEvents, IModuleEvent[] events) {
		// do nothing
	}
}