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
import org.eclipse.wst.server.core.util.ProgressUtil;
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
public class ResourceManager implements IResourceManager {
	private static final String SERVER_DATA_FILE = "servers.xml";
	private static final String SERVER_CONFIGURATION_DATA_FILE = "configurations.xml";

	private static ResourceManager instance;

	// currently active runtimes, servers, and server configurations
	protected List runtimes;
	protected List servers;
	protected List configurations;
	protected IRuntime defaultRuntime;

	// server resource change listeners
	protected transient List listeners;

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
			try {
				if (project.exists() && !project.hasNature(IServerProject.NATURE_ID)) {
					Trace.trace(Trace.RESOURCES, "Not a server project: " + project.getName());
					return;
				}
			} catch (CoreException e) {
				Trace.trace(Trace.SEVERE, "Could not verify project nature: " + project.getName() + " - " + e.getMessage());
			}
			
			if (!project.exists()) {
				Iterator iterator = ServerProjectNature.serverProjects.iterator();
				boolean found = false;
				while (iterator.hasNext()) {
					IProject serverProject = (IProject) iterator.next();
					if (serverProject.equals(project))
						found = true;
				}
				if (!found)
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
	
	class ModuleServerEvent implements IServerLifecycleEvent {
		IServer server;
		IModuleFactoryEvent[] factoryEvents;
		IModuleEvent[] events;
		ITask[] tasks;

		public IServer getServer() {
			return server;
		}

		public IModuleFactoryEvent[] getModuleFactoryEvents() {
			return factoryEvents;
		}

		public IModuleEvent[] getModuleEvents() {
			return events;
		}

		public ITask[] getTasks() {
			return tasks;
		}
	}

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
		ServerCore.getServerNatures();
		
		moduleFactoryListener = new ModuleFactoryListener();
		moduleListener = new ModuleListener();
		
		addResourceListener(ServerListener.getInstance());
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
		Iterator iterator = getServers().iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			try {
				if (server.getServerState() != IServer.SERVER_STOPPED) {
					IServerDelegate delegate = server.getDelegate();
					if (delegate instanceof IStartableServer && ((IStartableServer)delegate).isTerminateOnShutdown())
						((IStartableServer) delegate).terminate();
				}
			} catch (Exception e) { }
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace != null) {
			workspace.removeResourceChangeListener(modelResourceChangeListener);
			workspace.removeResourceChangeListener(publishResourceChangeListener);
		}
		
		ServerPlugin.getInstance().getPluginPreferences().removePropertyChangeListener(pcl);
		
		removeResourceListener(ServerListener.getInstance());
	}

	/**
	 * Adds a new server resource listener.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IServerResourceListener
	 */
	public void addResourceListener(IServerResourceListener listener) {
		Trace.trace(Trace.LISTENERS, "Adding server resource listener " + listener + " to " + this);
	
		if (listeners == null)
			listeners = new ArrayList();
		listeners.add(listener);
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
		fireServerResourceRemoved(runtime);
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

		// terminate server if it is being deleted!
		byte state = server.getServerState();
		if (state != IServer.SERVER_STOPPED && state != IServer.SERVER_STOPPING &&
				state != IServer.SERVER_UNKNOWN) {
			IServerDelegate delegate = server.getDelegate();
			if (delegate instanceof IStartableServer) {
				((IStartableServer) delegate).terminate();
			}
		}
		
		((Server) server).deleteLaunchConfigurations();
		ServerPlugin.getInstance().removeTempDirectory(server.getId(), new NullProgressMonitor());

		((Server)server).dispose();
		fireServerResourceRemoved(server);
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
		fireServerResourceRemoved(configuration);
		configurations.remove(configuration);
	}

	/**
	 * Fire a event because a new element has been added.
	 *
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	private void fireServerResourceAdded(final IElement element) {
		Trace.trace(Trace.LISTENERS, "->- Firing serverResourceAdded event: " + element.getName() + " ->-");
		
		if (listeners == null || listeners.isEmpty())
			return;
	
		int size = listeners.size();
		IServerResourceListener[] srl = new IServerResourceListener[size];
		listeners.toArray(srl);
	
		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.LISTENERS, "  Firing serverResourceAdded event to " + srl[i]);
			try {
				if (element instanceof IRuntime)
					srl[i].runtimeAdded((IRuntime) element);
				else if (element instanceof IServer)
					srl[i].serverAdded((IServer) element);
				else
					srl[i].serverConfigurationAdded((IServerConfiguration) element);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing serverResourceAdded event to " + srl[i], e);
			}
		}
		Trace.trace(Trace.LISTENERS, "-<- Done firing serverResourceAdded event -<-");
	}

	/**
	 * Fire a event because a resource has been changed.
	 *
	 * @param resource org.eclipse.wst.server.core.model.IServerResource
	 */
	private void fireServerResourceChanged(final IElement element) {
		Trace.trace(Trace.LISTENERS, "->- Firing serverResourceChanged event: " + element.getName() + " ->-");
	
		if (listeners == null || listeners.isEmpty())
			return;
	
		int size = listeners.size();
		IServerResourceListener[] srl = new IServerResourceListener[size];
		listeners.toArray(srl);
	
		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.LISTENERS, "  Firing serverResourceChanged event to " + srl[i]);
			try {
				if (element instanceof IRuntime)
					srl[i].runtimeChanged((IRuntime) element);
				else if (element instanceof IServer)
					srl[i].serverChanged((IServer) element);
				else
					srl[i].serverConfigurationChanged((IServerConfiguration) element);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing serverResourceChanged event to " + srl[i], e);
			}
		}
	
		Trace.trace(Trace.LISTENERS, "-<- Done firing serverResourceChanged event -<-");
	}

	/**
	 * Fire a event because a resource has been removed. Note
	 * that although the physical resource no longer exists,
	 * the element will remain in the resource manager until
	 * after this event is fired.
	 *
	 * @param info org.eclipse.wst.server.core.model.IServerResource
	 */
	private void fireServerResourceRemoved(final IElement element) {
		Trace.trace(Trace.LISTENERS, "->- Firing serverResourceRemoved event: " + element.getName() + " ->-");
	
		if (listeners == null || listeners.isEmpty())
			return;
	
		int size = listeners.size();
		IServerResourceListener[] srl = new IServerResourceListener[size];
		listeners.toArray(srl);
	
		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.LISTENERS, "  Firing serverResourceRemoved event to " + srl[i]);
			try {
				if (element instanceof IRuntime)
					srl[i].runtimeRemoved((IRuntime) element);
				else if (element instanceof IServer)
					srl[i].serverRemoved((IServer) element);
				else
					srl[i].serverConfigurationRemoved((IServerConfiguration) element);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing serverResourceRemoved event to " + srl[i], e);
			}
		}
	
		Trace.trace(Trace.LISTENERS, "-<- Done firing serverResourceRemoved event -<-");
	}

	/**
	 * Returns the server configuration that came from the
	 * given resource.
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 * @return org.eclipse.wst.server.core.model.IServerConfiguration
	 */
	public IServerConfiguration getServerConfiguration(IFile file) {
		if (file == null)
			return null;
		
		Iterator iterator = configurations.iterator();
		while (iterator.hasNext()) {
			IServerConfiguration config = (IServerConfiguration) iterator.next();
			if (file.equals(config.getFile()))
				return config;
		}
		return null;
	}

	/**
	 * Returns a list of all currently active server configurations.
	 *
	 * @return java.util.List
	 */
	public List getServerConfigurations() {
		List list = new ArrayList(configurations);
		return ServerPlugin.sortServerResourceList(list);
	}
	
	/**
	 * Returns a list of all servers configs.
	 *
	 * @return java.util.List
	 */
	public List getServerConfigurations(IServerConfigurationType configType) {
		List list = new ArrayList();
		Iterator iterator = configurations.iterator();
		while (iterator.hasNext()) {
			IServerConfiguration config = (IServerConfiguration) iterator.next();
			if (config.getServerConfigurationType().equals(configType))
				list.add(config);
		}
		return list;
	}
	
	/**
	 * Returns a list of all servers.
	 *
	 * @return java.util.List
	 */
	public IServerConfiguration getServerConfiguration(String id) {
		if (id == null)
			return null;
	
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
				} catch (Exception ex) { }
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
		if (!runtimes.contains(runtime))
			registerRuntime(runtime);
		else
			fireServerResourceChanged(runtime);
		saveRuntimesList();
		resolveServers();
	}

	protected void removeRuntime(IRuntime runtime) {
		if (runtimes.contains(runtime)) {
			deregisterRuntime(runtime);
			saveRuntimesList();
			resolveServers();
		}
	}

	protected void addServer(IServer server) {
		if (!servers.contains(server))
			registerServer(server);
		else
			fireServerResourceChanged(server);
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
			fireServerResourceChanged(config);
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
	 * Returns a list of all runtimes.
	 *
	 * @return java.util.List
	 */
	public List getRuntimes() {
		List list = new ArrayList(runtimes);
		
		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				IRuntime a = (IRuntime) list.get(i);
				IRuntime b = (IRuntime) list.get(j);
				if (a.getRuntimeType().getOrder() < b.getRuntimeType().getOrder()) {
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
		
		return list;
	}
	
	/**
	 * Returns the runtimes with the given runtime type.
	 *
	 * @return java.util.List
	 */
	public List getRuntimes(IRuntimeType runtimeType) {
		List list = new ArrayList();
		Iterator iterator = runtimes.iterator();
		while (iterator.hasNext()) {
			IRuntime runtime = (IRuntime) iterator.next();
			if (runtime.getRuntimeType() != null && runtime.getRuntimeType().equals(runtimeType))
				list.add(runtime);
		}
		return list;
	}
	
	/**
	 * Returns the runtime with the given id.
	 *
	 * @return IRuntime
	 */
	public IRuntime getRuntime(String id) {
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
	 * Returns the server that came from the
	 * given resource.
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 * @return org.eclipse.wst.server.core.model.IServer
	 */
	public IServer getServer(IFile file) {
		if (file == null)
			return null;
		Iterator iterator = servers.iterator();
		while (iterator.hasNext()) {
			Server server = (Server) iterator.next();
			if (file.equals(server.getFile()))
				return server;
		}
		return null;
	}

	/**
	 * Returns a list of all servers.
	 *
	 * @return java.util.List
	 */
	public List getServers() {
		List list = new ArrayList(servers);
		return ServerPlugin.sortServerResourceList(list);
	}

	/**
	 * Returns a list of all servers.
	 *
	 * @return java.util.List
	 */
	public IServer getServer(String id) {
		if (id == null)
			return null;
	
		Iterator iterator = servers.iterator();
		while (iterator.hasNext()) {
			Server server = (Server) iterator.next();
			if (id.equals(server.getId()))
				return server;
		}
		return null;
	}
	
	/**
	 * Returns a list of all servers.
	 *
	 * @return java.util.List
	 */
	public List getServers(IServerType serverType) {
		List list = new ArrayList();
		Iterator iterator = servers.iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			if (server.getServerType().equals(serverType))
				list.add(server);
		}
		return list;
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
		} else
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
	
		IServer server = getServer(file);
		if (server != null) {
			found = true;
			try {
				Trace.trace(Trace.RESOURCES, "Reloading server: " + server);
				((Server) server).loadFromFile(monitor);
				fireServerResourceChanged(server);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error reloading server " + server.getName() + " from " + file + ": " + e.getMessage());
				deregisterServer(server);
			}
		}
		
		IServerConfiguration configuration = getServerConfiguration(file);
		if (configuration != null) {
			found = true;
			try {
				Trace.trace(Trace.RESOURCES, "Reloading configuration: " + configuration);
				((ServerConfiguration) configuration).loadFromFile(monitor);
				fireServerResourceChanged(configuration);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error reloading configuration " + configuration.getName() + " from " + file + ": " + e.getMessage());
				deregisterServerConfiguration(configuration);
			}

			// find any running servers that contain this configuration
			// notify the servers with this configuration
			Iterator iterator = getServers().iterator();
			while (iterator.hasNext()) {
				IServer server2 = (IServer) iterator.next();
				if (server2.getServerConfiguration().equals(configuration))
					server2.updateConfiguration();
			}
			fireServerResourceChanged(configuration);
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
	
		IServer server = getServer(file);
		if (server != null) {
			deregisterServer(server);
			return true;
		}
		IServerConfiguration config = getServerConfiguration(file);
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

		final IProjectModule moduleProject = ServerUtil.getModuleProject(project);
		if (moduleProject == null)
			return;
		
		Trace.trace(Trace.FINEST, "- publishHandleProjectChange");

		Iterator iterator = getServers().iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			if (server.isDelegateLoaded())
				((Server) server).handleModuleProjectChange(delta, new IProjectModule[] { moduleProject });
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
		} catch (Exception e) { }
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
		fireServerResourceAdded(runtime);
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
		fireServerResourceAdded(server);
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
		fireServerResourceAdded(config);
	}

	/**
	 * Removes an existing server resource listener.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IServerResourceListener
	 */
	public void removeResourceListener(IServerResourceListener listener) {
		Trace.trace(Trace.LISTENERS, "Removing server resource listener " + listener + " from " + this);
	
		if (listeners != null)
			listeners.remove(listener);
	}

	/**
	 *
	 */
	protected void addModuleFactoryListener(IModuleFactoryDelegate delegate) {
		if (delegate == null)
			return;
	
		Trace.trace(Trace.LISTENERS, "Adding module factory listener to: " + delegate);
		delegate.addModuleFactoryListener(moduleFactoryListener);
		
		List modules = delegate.getModules();
		if (modules != null) {
			Iterator iterator = modules.iterator();
			while (iterator.hasNext()) {
				IModule module = (IModule) iterator.next();
				Trace.trace(Trace.LISTENERS, "Adding module listener to: " + module);
				module.addModuleListener(moduleListener);
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
		if (moduleServerEventHandlers == null)
			return;
		
		List list = new ArrayList();

		Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			
			if (server.isDelegateLoaded()) {
				// make sure it is reloaded
				try {
					if (server.getFile() != null)
						server.getFile().refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
				} catch (Exception e) { }
	
				ITask[] tasks = server.getRepairCommands(factoryEvents, events);
				if (tasks != null && tasks.length > 0) {
					ModuleServerEvent mse = new ModuleServerEvent();
					mse.server = server;
					mse.tasks = tasks;
					mse.factoryEvents = factoryEvents; 
					mse.events = events;
					list.add(mse);
				}
			}
		}
		
		if (list.isEmpty())
			return;
		
		ModuleServerEvent[] msEvents = new ModuleServerEvent[list.size()];
		list.toArray(msEvents);
	
		iterator = moduleServerEventHandlers.iterator();
		while (iterator.hasNext()) {
			IServerLifecycleEventHandler handler = (IServerLifecycleEventHandler) iterator.next();
			try {
				boolean[] result = null;
				Trace.trace(Trace.LISTENERS, "  Firing moduleServerEvents to " + handler);
				result = handler.handleModuleServerEvents(msEvents);
				if (result != null && result.length == msEvents.length) {
					List list2 = new ArrayList();
					int size = result.length;
					for (int i = 0; i < size; i++) {
						if (!result[i])
							list2.add(msEvents[i]);
					}
					msEvents = new ModuleServerEvent[list2.size()];
					list2.toArray(msEvents);
				}
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing moduleServerEvents to " + handler);
			}
		}
	}
	
	public void addServerLifecycleEventHandler(int index, IServerLifecycleEventHandler handler) {
		if (moduleServerEventHandlers == null) {
			moduleServerEventHandlers = new ArrayList();
			moduleServerEventHandlerIndexes = new ArrayList();
		}
		
		int ind = 0;
		int size = moduleServerEventHandlers.size();
		while (ind < size && ((Integer) moduleServerEventHandlerIndexes.get(ind)).intValue() < index) {
			ind++;
		}
		
		moduleServerEventHandlers.add(ind, handler);
		moduleServerEventHandlerIndexes.add(ind, new Integer(index));
	}

	public void removeServerLifecycleEventHandler(IServerLifecycleEventHandler handler) {
		if (moduleServerEventHandlers == null)
			return;
		
		int ind = moduleServerEventHandlers.indexOf(handler);
		if (ind >= 0) {
			moduleServerEventHandlers.remove(ind);
			moduleServerEventHandlerIndexes.remove(ind);
		}
	}
}