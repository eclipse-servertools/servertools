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
package org.eclipse.wst.server.core;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.internal.*;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.util.ProgressUtil;
/**
 * Base class for obtaining references to server models.
 * 
 * Also provides references for servers and server configurations.
 * These references can be saved as tool or server-data and can be
 * used to return the original resource if it still exists. These
 * references are not OS specific and can be used in a team environment.
 */
public class ServerCore {
	// server core plugin id
	public static final String PLUGIN_ID = "org.eclipse.wst.server.core";

	// cached copy of all server startups
	private static List startups;

	// cached copy of all module factories
	private static List moduleFactories;

	// cached copy of all module object adapters
	private static List moduleObjectAdapters;

	// cached copy of all launchable adapters
	private static List launchableAdapters;

	// cached copy of all launchable clients
	private static List launchableClients;
	
	// cached copy of all module tasks
	private static List moduleTasks;
	
	// cached copy of all server tasks
	private static List serverTasks;
	
	//	cached copy of all module kinds
	private static List moduleKinds;
	
	//	cached copy of all runtime types
	private static List runtimeTypes;
	
	//	cached copy of all runtime target handlers
	private static List runtimeTargetHandlers;
	
	//	cached copy of all runtime locators
	private static List runtimeLocators;

	//	cached copy of all server and configuration types
	private static List serverTypes;
	private static List serverConfigurationTypes;
	
	//	cached copy of all monitors
	private static List monitors;

	// cached copy of the server publisher classes
	// keyed from String id to IPublishManager
	private static Map publishManagers;
	
	// preferences
	private static IServerPreferences preferences;

	/**
	 * ServerCore constructor comment.
	 */
	private ServerCore() {
		super();
	}

	/**
	 * Returns a map of all publishManagers, keyed by String id.
	 *
	 * @return java.util.Map
	 */
	public static Map getPublishManagers() {
		if (publishManagers == null)
			loadPublishManagers();
		return publishManagers;
	}

	/**
	 * Returns the publish manager with the given id.
	 *
	 * @return org.eclipse.wst.server.core.IPublishManager
	 */
	public static IPublishManager getPublishManager(String id) {
		if (publishManagers == null)
			loadPublishManagers();
			
		try {
			return (IPublishManager) publishManagers.get(id);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Returns the resource manager.
	 *
	 * @return org.eclipse.wst.server.core.IResourceManager
	 */
	public static IResourceManager getResourceManager() {
		return ResourceManager.getInstance();
	}
	
	/**
	 * Returns the server monitor manager.
	 *
	 * @return org.eclipse.wst.server.core.IServerMonitorManager
	 */
	public static IServerMonitorManager getServerMonitorManager() {
		return ServerMonitorManager.getInstance();
	}

	/**
	 * Returns the preference information for the server core plugin.
	 *
	 * @return org.eclipse.wst.server.core.IServerPreferences
	 */
	public static IServerPreferences getServerPreferences() {
		if (preferences == null)
			preferences = new ServerPreferences();
		return preferences;
	}
	
	/**
	 * Returns the preference information for the project.
	 *
	 * @return org.eclipse.wst.server.core.IServerProjectPreferences
	 */
	public static IProjectProperties getProjectProperties(IProject project) {
		return new ProjectProperties(project);
	}
	
	/**
	 * Returns a List of all startups.
	 *
	 * @return java.util.List
	 */
	public static List getStartups() {
		if (startups == null)
			loadStartups();
		return startups;
	}
	
	/**
	 * Returns a List of all module kinds.
	 *
	 * @return java.util.List
	 */
	public static List getModuleKinds() {
		if (moduleKinds == null)
			loadModuleKinds();
		return moduleKinds;
	}
	
	/**
	 * Returns the module kind with the given id.
	 *
	 * @return org.eclipse.wst.server.core.IModuleKind
	 */
	public static IModuleKind getModuleKind(String id) {
		if (id == null)
			return null;

		if (moduleKinds == null)
			loadModuleKinds();
		
		Iterator iterator = moduleKinds.iterator();
		while (iterator.hasNext()) {
			IModuleKind moduleType = (IModuleKind) iterator.next();
			if (id.equals(moduleType.getId()))
				return moduleType;
		}
		return null;
	}
	
	/**
	 * Returns a List of all runtime types.
	 *
	 * @return java.util.List
	 */
	public static List getRuntimeTypes() {
		if (runtimeTypes == null)
			loadRuntimeTypes();
		return runtimeTypes;
	}
	
	/**
	 * Returns the runtime type with the given id.
	 *
	 * @return org.eclipse.wst.server.core.IRuntimeType
	 */
	public static IRuntimeType getRuntimeType(String id) {
		if (id == null)
			return null;

		if (runtimeTypes == null)
			loadRuntimeTypes();
		
		Iterator iterator = runtimeTypes.iterator();
		while (iterator.hasNext()) {
			IRuntimeType runtimeType = (IRuntimeType) iterator.next();
			if (id.equals(runtimeType.getId()))
				return runtimeType;
		}
		return null;
	}
	
	/**
	 * Returns a List of all runtime locators.
	 *
	 * @return java.util.List
	 */
	public static List getRuntimeLocators() {
		if (runtimeLocators == null)
			loadRuntimeLocators();
		return runtimeLocators;
	}
	
	/**
	 * Returns a List of all runtime target handlers.
	 *
	 * @return java.util.List
	 */
	public static List getRuntimeTargetHandlers() {
		if (runtimeTargetHandlers == null)
			loadRuntimeTargetHandlers();
		return runtimeTargetHandlers;
	}
	
	/**
	 * Returns the runtime target handler with the given id.
	 *
	 * @return org.eclipse.wst.server.core.IRuntimeTargetHandler
	 */
	public static IRuntimeTargetHandler getRuntimeTargetHandler(String id) {
		if (id == null)
			return null;

		if (runtimeTargetHandlers == null)
			loadRuntimeTargetHandlers();
		
		Iterator iterator = runtimeTargetHandlers.iterator();
		while (iterator.hasNext()) {
			IRuntimeTargetHandler runtimeTargetListener = (IRuntimeTargetHandler) iterator.next();
			if (id.equals(runtimeTargetListener.getId()))
				return runtimeTargetListener;
		}
		return null;
	}

	/**
	 * Returns a List of all server types.
	 *
	 * @return java.util.List
	 */
	public static List getServerTypes() {
		if (serverTypes == null)
			loadServerTypes();
		return serverTypes;
	}
	
	/**
	 * Returns the server type with the given id.
	 *
	 * @return org.eclipse.wst.server.core.IServerType
	 */
	public static IServerType getServerType(String id) {
		if (id == null)
			return null;

		if (serverTypes == null)
			loadServerTypes();
		
		Iterator iterator = serverTypes.iterator();
		while (iterator.hasNext()) {
			IServerType serverType = (IServerType) iterator.next();
			if (id.equals(serverType.getId()))
				return serverType;
		}
		return null;
	}
	
	/**
	 * Returns a List of all server configuration types.
	 *
	 * @return java.util.List
	 */
	public static List getServerConfigurationTypes() {
		if (serverConfigurationTypes == null)
			loadServerConfigurationTypes();
		return serverConfigurationTypes;
	}
	
	/**
	 * Returns the server configuration type with the given id.
	 *
	 * @return org.eclipse.wst.server.core.IServerConfigurationType
	 */
	public static IServerConfigurationType getServerConfigurationType(String id) {
		if (id == null)
			return null;

		if (serverConfigurationTypes == null)
			loadServerConfigurationTypes();
		
		Iterator iterator = serverConfigurationTypes.iterator();
		while (iterator.hasNext()) {
			IServerConfigurationType serverConfigurationType = (IServerConfigurationType) iterator.next();
			if (id.equals(serverConfigurationType.getId()))
				return serverConfigurationType;
		}
		return null;
	}

	/**
	 * Returns a List of all module factories.
	 *
	 * @return java.util.List
	 */
	public static List getModuleFactories() {
		if (moduleFactories == null)
			loadModuleFactories();
		return moduleFactories;
	}
	
	/**
	 * Returns the module factory.
	 *
	 * @return java.util.List
	 */
	public static IModuleFactory getModuleFactory(String id) {
		if (id == null)
			return null;

		if (moduleFactories == null)
			loadModuleFactories();
		
		Iterator iterator = moduleFactories.iterator();
		while (iterator.hasNext()) {
			IModuleFactory factory = (IModuleFactory) iterator.next();
			if (id.equals(factory.getId()))
				return factory;
		}
		return null;
	}

	/**
	 * Returns a List of all module object adapters.
	 *
	 * @return java.util.List
	 */
	public static List getModuleObjectAdapters() {
		if (moduleObjectAdapters == null)
			loadModuleObjectAdapters();
		return moduleObjectAdapters;
	}
	
	/**
	 * Returns a List of all launchable adapters.
	 *
	 * @return java.util.List
	 */
	public static List getLaunchableAdapters() {
		if (launchableAdapters == null)
			loadLaunchableAdapters();
		return launchableAdapters;
	}
	
	/**
	 * Returns a List of all launchable clients.
	 *
	 * @return java.util.List
	 */
	public static List getLaunchableClients() {
		if (launchableClients == null)
			loadLaunchableClients();
		return launchableClients;
	}
	
	/**
	 * Returns a List of all module tasks.
	 *
	 * @return java.util.List
	 */
	public static List getModuleTasks() {
		if (moduleTasks == null)
			loadModuleTasks();
		return moduleTasks;
	}
	
	/**
	 * Returns a List of all server tasks.
	 *
	 * @return java.util.List
	 */
	public static List getServerTasks() {
		if (serverTasks == null)
			loadServerTasks();
		return serverTasks;
	}
	
	/**
	 * Returns a List of all server monitors.
	 *
	 * @return java.util.List
	 */
	public static List getServerMonitors() {
		if (monitors == null)
			loadServerMonitors();
		return monitors;
	}

	/**
	 * Returns a list of all open server projects (IServerProjects)
	 * in the workbench.
	 *
	 * @return java.util.List
	 */
	public static List getServerNatures() {
		try {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
	
			List list = new ArrayList();
			int size = projects.length;
			for (int i = 0; i < size; i++) {
				try {
					if (projects[i].isOpen() && projects[i].hasNature(IServerProject.NATURE_ID))
						list.add(projects[i].getNature(IServerProject.NATURE_ID));
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error adding server nature", e);
				}
			}
	
			return list;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error getting server natures", e);
			return new ArrayList();
		}
	}

	/**
	 * Returns a list of all server projects (IProjects) in
	 * the workbench.
	 *
	 * @return java.util.List
	 */
	public static List getServerProjects() {
		try {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
	
			List list = new ArrayList();
			int size = projects.length;
			for (int i = 0; i < size; i++) {
				try {
					if (projects[i].hasNature(IServerProject.NATURE_ID))
						list.add(projects[i]);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error adding server nature project", e);
				}
			}

			return list;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error getting server nature projects", e);
			return new ArrayList();
		}
	}
	
	/**
	 * Add the given nature to the project.
	 *
	 * @param monitor
	 * @param project org.eclipse.core.resources.IProject
	 */
	private static boolean addNature(IProject project, String natureId, IProgressMonitor monitor) {
		if (project == null)
			return false;

		try {
			monitor = ProgressUtil.getMonitorFor(monitor);

			// make sure the project is open
			if (!project.isOpen()) {
				monitor.beginTask(ServerPlugin.getResource("%createServerProjectTask"), 2000);
				project.open(ProgressUtil.getSubMonitorFor(monitor, 1000));
			} else
				monitor.beginTask(ServerPlugin.getResource("%createServerProjectTask"), 1000);
		
			// get the current natures
			IProjectDescription desc = project.getDescription();
			String[] natureIds = desc.getNatureIds();
			if (natureIds == null)
				natureIds = new String[0];
	
			// check that the nature isn't already there..
			int size = natureIds.length;
			for (int i = 0; i < size; i++) {
				if (natureId.equals(natureIds[i]))
					return true;
			}
	
			// otherwise, add the new nature
			String[] newNatureIds = new String[size + 1];
			System.arraycopy(natureIds, 0, newNatureIds, 0, size);
			newNatureIds[size] = natureId;
			desc.setNatureIds(newNatureIds);
	
			project.setDescription(desc, ProgressUtil.getSubMonitorFor(monitor, 1000));
			return true;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not add nature to " + project.getName(), e);
			return false;
		} finally {
			monitor.done();
		}
	}

	/**
	 * Creates a new server project with the given name. If path is
	 * null, it will be created in the default location.
	 *
	 * @param name java.lang.String
	 * @param path org.eclipse.core.resource.IPath
	 * @param monitor
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public static IStatus createServerProject(String name, IPath path, IProgressMonitor monitor) {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(ServerPlugin.getResource("%createServerProjectTask"), 3000);

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject project = workspace.getRoot().getProject(name);
	
			// get a project descriptor
			IProjectDescription description = workspace.newProjectDescription(name);
			description.setLocation(path);
	
			project.create(description, ProgressUtil.getSubMonitorFor(monitor, 1000));
			if (monitor.isCanceled())
				return null;
			project.open(ProgressUtil.getSubMonitorFor(monitor, 1000));
			if (monitor.isCanceled())
				return null;

			// add the server project nature
			addNature(project, IServerProject.NATURE_ID, ProgressUtil.getSubMonitorFor(monitor, 1000));
	
			if (monitor.isCanceled())
				return null;
	
			return new Status(IStatus.OK, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%serverProjectCreated"), null);
		} catch (CoreException ce) {
			Trace.trace(Trace.SEVERE, "Could not create server project named " + name, ce);
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorCouldNotCreateServerProjectStatus", ce.getMessage()), ce);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not create server project (2) named " + name, e);
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorCouldNotCreateServerProject"), e);
		} finally {
			monitor.done();
		}
	}

	/**
	 * Load the server startups.
	 */
	private static void loadStartups() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .startup extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "startup");

		int size = cf.length;
		startups = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				IStartup startup = (IStartup) cf[i].createExecutableExtension("class");
				startups.add(startup);
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded startup: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load startup: " + cf[i].getAttribute("id"), t);
			}
		}
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .startup extension point -<-");
	}
	
	/**
	 * Load the module kinds.
	 */
	private static void loadModuleKinds() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .moduleKinds extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "moduleKinds");

		int size = cf.length;
		moduleKinds = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				ModuleKind moduleKind = new ModuleKind(cf[i]);
				moduleKinds.add(moduleKind);
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded moduleKind: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load moduleKind: " + cf[i].getAttribute("id"), t);
			}
		}
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .moduleKinds extension point -<-");
	}

	/**
	 * Load the runtime types.
	 */
	private static void loadRuntimeTypes() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .runtimeTypes extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "runtimeTypes");

		int size = cf.length;
		runtimeTypes = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				RuntimeType runtimeType = new RuntimeType(cf[i]);
				runtimeTypes.add(runtimeType);
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded runtimeType: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeType: " + cf[i].getAttribute("id"), t);
			}
		}
		ServerUtil.sortOrderedList(runtimeTypes);
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .runtimeTypes extension point -<-");
	}
	
	/**
	 * Load the runtime locators.
	 */
	private static void loadRuntimeLocators() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .runtimeLocators extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "runtimeLocators");

		int size = cf.length;
		runtimeLocators = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				RuntimeLocator runtimeLocator = new RuntimeLocator(cf[i]);
				runtimeLocators.add(runtimeLocator);
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded runtimeLocator: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeLocator: " + cf[i].getAttribute("id"), t);
			}
		}
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .runtimeLocators extension point -<-");
	}
	
	/**
	 * Load the runtime target listeners.
	 */
	private static void loadRuntimeTargetHandlers() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .runtimeTargetHandlers extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "runtimeTargetHandlers");

		int size = cf.length;
		runtimeTargetHandlers = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				RuntimeTargetHandler runtimeTargetListener = new RuntimeTargetHandler(cf[i]);
				runtimeTargetHandlers.add(runtimeTargetListener);
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded runtimeTargetHandler: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeTargetHandler: " + cf[i].getAttribute("id"), t);
			}
		}
		ServerUtil.sortOrderedList(runtimeTargetHandlers);
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .runtimeTargetHandlers extension point -<-");
	}

	/**
	 * Load the server types.
	 */
	private static void loadServerTypes() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .serverTypes extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "serverTypes");

		int size = cf.length;
		serverTypes = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				ServerType serverType = new ServerType(cf[i]);
				serverTypes.add(serverType);
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded serverType: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load serverType: " + cf[i].getAttribute("id"), t);
			}
		}
		ServerUtil.sortOrderedList(serverTypes);
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .serverTypes extension point -<-");
	}

	/**
	 * Load the server configuration types.
	 */
	private static void loadServerConfigurationTypes() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .serverConfigurationTypes extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "serverConfigurationTypes");

		int size = cf.length;
		serverConfigurationTypes = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				ServerConfigurationType serverConfigurationType = new ServerConfigurationType(cf[i]);
				serverConfigurationTypes.add(serverConfigurationType);
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded serverConfigurationType: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load serverConfigurationType: " + cf[i].getAttribute("id"), t);
			}
		}
		ServerUtil.sortOrderedList(serverConfigurationTypes);
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .serverConfigurationTypes extension point -<-");
	}

	/**
	 * Load the publish manager extension point.
	 */
	private static void loadPublishManagers() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .publish extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "publish");

		int size = cf.length;
		publishManagers = new HashMap(size);
		for (int i = 0; i < size; i++) {
			try {
				String id = cf[i].getAttribute("id");
				IPublishManager publisher = new PublishManager(cf[i]);
				publishManagers.put(id, publisher);
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded publish manager: " + id);
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load publish manager: " + cf[i].getAttribute("class"), t);
			}
		}
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .publisher extension point -<-");
	}

	/**
	 * Load the module factories extension point.
	 */
	private static void loadModuleFactories() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .moduleFactories extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "moduleFactories");

		int size = cf.length;
		moduleFactories = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				moduleFactories.add(new ModuleFactory(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded moduleFactories: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load moduleFactories: " + cf[i].getAttribute("id"), t);
			}
		}
		ServerUtil.sortOrderedList(moduleFactories);
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .moduleFactories extension point -<-");
	}

	/**
	 * Load the module object adapters extension point.
	 */
	private static void loadModuleObjectAdapters() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .moduleObjectAdapters extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "moduleObjectAdapters");

		int size = cf.length;
		moduleObjectAdapters = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				moduleObjectAdapters.add(new ModuleObjectAdapter(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded moduleObjectAdapter: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load moduleObjectAdapter: " + cf[i].getAttribute("id"), t);
			}
		}
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .moduleObjectAdapters extension point -<-");
	}
	
	/**
	 * Load the launchable adapters extension point.
	 */
	private static void loadLaunchableAdapters() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .launchableAdapters extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "launchableAdapters");

		int size = cf.length;
		launchableAdapters = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				launchableAdapters.add(new LaunchableAdapter(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded launchableAdapter: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load launchableAdapter: " + cf[i].getAttribute("id"), t);
			}
		}
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .launchableAdapters extension point -<-");
	}

	/**
	 * Load the launchable client extension point.
	 */
	private static void loadLaunchableClients() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .clients extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "clients");

		int size = cf.length;
		launchableClients = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				launchableClients.add(new Client(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded clients: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load clients: " + cf[i].getAttribute("id"), t);
			}
		}
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .clients extension point -<-");
	}
	
	/**
	 * Load the module task extension point.
	 */
	private static void loadModuleTasks() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .moduleTasks extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "moduleTasks");

		int size = cf.length;
		moduleTasks = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				moduleTasks.add(new ModuleTask(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded moduleTask: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load moduleTask: " + cf[i].getAttribute("id"), t);
			}
		}
		ServerUtil.sortOrderedList(moduleTasks);
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .moduleTasks extension point -<-");
	}

	/**
	 * Load the server task extension point.
	 */
	private static void loadServerTasks() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .serverTasks extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "serverTasks");

		int size = cf.length;
		serverTasks = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				serverTasks.add(new ServerTask(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded serverTask: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load serverTask: " + cf[i].getAttribute("id"), t);
			}
		}
		
		ServerUtil.sortOrderedList(serverTasks);
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .serverTasks extension point -<-");
	}

	/**
	 * Load the server monitor extension point.
	 */
	private static void loadServerMonitors() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .serverMonitors extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerCore.PLUGIN_ID, "serverMonitors");

		int size = cf.length;
		monitors = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				monitors.add(new ServerMonitor(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded serverMonitor: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load serverMonitor: " + cf[i].getAttribute("id"), t);
			}
		}
	
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .serverMonitors extension point -<-");
	}
}
