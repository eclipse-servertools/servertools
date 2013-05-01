/**********************************************************************
 * Copyright (c) 2003, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.debug.core.ILaunchConfiguration;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.internal.*;
/**
 * Server utility methods. These static methods can be used to perform
 * common operations on server artifacts.
 * <p>
 * This class provides all its functionality through static members.
 * It is not intended to be sub-classed or instantiated.
 * </p>
 * @since 1.0
 */
public class ServerUtil {
	/**
	 * Constant identifying the job family identifier for server operations.
	 * 
	 * @see org.eclipse.core.runtime.jobs.IJobManager#join(Object, IProgressMonitor)
	 * @since 2.0
	 */
	public static final Object SERVER_JOB_FAMILY = ServerPlugin.PLUGIN_ID;

	/**
	 * Static utility class - cannot create an instance.
	 */
	private ServerUtil() {
		// can't create
	}

	/**
	 * Returns the module contained within the given project. If more than one module
	 * is contained with the project, this method will return an arbitrary module
	 * unless the module factory defines an ordering. If there might be multiple
	 * modules in a project, users should typically use getModules(IProject) instead.
	 * <p>
	 * This method may trigger bundle loading and is not suitable for
	 * short/UI operations.
	 * </p>
	 * 
	 * @param project a project
	 * @return a module that is contained with the project, or null if no
	 *    modules are contained in the given project
	 * @see #getModules(IProject)
	 */
	public static IModule getModule(IProject project) {
		if (project == null)
			throw new IllegalArgumentException();
		
		IModule[] modules = getModules(project);
		if (modules != null && modules.length > 0)
			return modules[0];
		
		return null;
	}

	/**
	 * Returns the modules contained within the given project.
	 * <p>
	 * This method may trigger bundle loading and is not suitable for
	 * short/UI operations.
	 * </p>
	 * 
	 * @param project a project
	 * @return a possibly-empty array of modules
	 * @see #getModule(IProject)
	 */
	public static IModule[] getModules(IProject project) {
		if (project == null)
			throw new IllegalArgumentException();
		
		// use a set for better contains() performance
		List<IModule> list = new ArrayList<IModule>();
		
		ModuleFactory[] factories = ServerPlugin.getModuleFactories();

		if (factories != null) {
			for (ModuleFactory factory : factories) {
				if (factory.isEnabled(project, null)){
					IModule[] modules = factory.getModules(project, null);
					if (modules != null) {
						for (IModule module : modules) {
							if (!list.contains(module))
								list.add(module);
						}
					}
				}
			}
		}
		return list.toArray(new IModule[list.size()]);
	}

	/**
	 * Returns the module with the given moduleId, if one exists. The moduleId
	 * must not be null.
	 * <p>
	 * This method may trigger bundle loading and is not suitable for
	 * short/UI operations.
	 * </p>
	 * 
	 * @param moduleId a module id
	 * @return the module, or <code>null</code> if the module could not be found
	 */
	public static IModule getModule(String moduleId) {
		if (moduleId == null)
			throw new IllegalArgumentException();
		
		int index = moduleId.indexOf(":");
		if (index <= 0)
			return null;
		
		String factoryId = moduleId.substring(0, index);
		ModuleFactory moduleFactory = ServerPlugin.findModuleFactory(factoryId);
		if (moduleFactory == null)
			return null;
		
		String moduleSubId = moduleId.substring(index+1);
		return moduleFactory.findModule(moduleSubId, null);
	}

	/**
	 * Get the display name of a given module
	 * @return the display name
	 * @since 1.5
	 */
	public static String getModuleDisplayName(IModule curModule) {
		if (curModule instanceof IModule2) {
			String displayProp = ((IModule2)curModule).getProperty(IModule2.PROP_DISPLAY_NAME);
			if (displayProp != null) {
				return displayProp;
			}
		}
		return curModule.getName();
	}

	/**
	 * Return all the available modules from all factories whose
	 * type matches the given module types.
	 * <p>
	 * This method may trigger bundle loading and is not suitable for
	 * short/UI operations. It also performs a search of all available
	 * modules of the given types, and due to performance reasons should
	 * not be used unless absolutely required.
	 * </p>
	 * 
	 * @param moduleTypes an array of module types
	 * @return a possibly empty array of modules
	 */
	public static IModule[] getModules(IModuleType[] moduleTypes) {
		List<IModule> list = new ArrayList<IModule>();
		
		ModuleFactory[] factories = ServerPlugin.getModuleFactories();
		if (factories != null) {
			for (ModuleFactory factory : factories) {
				if (isSupportedModule(factory.getModuleTypes(), moduleTypes)) {
					IModule[] modules = factory.getModules(null);
					if (modules != null) {
						for (IModule module : modules)
							list.add(module);
					}
				}
			}
		}
		IModule[] modules = new IModule[list.size()];
		list.toArray(modules);
		return modules;
	}

	/**
	 * Return all the available modules from all factories whose
	 * type matches the given module type id.
	 * <p>
	 * This method may trigger bundle loading and is not suitable for
	 * short/UI operations. It also performs a search of all available
	 * modules of this type, and due to performance reasons should not
	 * be used unless absolutely required.
	 * </p>
	 * 
	 * @param type a module type
	 * @return a possibly empty array of modules
	 */
	public static IModule[] getModules(String type) {
		List<IModule> list = new ArrayList<IModule>();
		
		ModuleFactory[] factories = ServerPlugin.getModuleFactories();
		if (factories != null) {
			for (ModuleFactory factory : factories) {
				if (isSupportedModule(factory.getModuleTypes(), type, null)) {
					IModule[] modules = factory.getModules(null);
					if (modules != null) {
						for (IModule module : modules)
							if (type.equals(module.getModuleType().getId()))
								list.add(module);
					}
				}
			}
		}
		IModule[] modules = new IModule[list.size()];
		list.toArray(modules);
		return modules;
	}

	/**
	 * Returns <code>true</code> if any of the given moduleTypes have the given
	 * module type id and version id.
	 * 
	 * @param moduleTypes an array of module types, may not be null
	 * @param typeId a module type id, or null for any module type
	 * @param versionId a module version, or null for any version
	 * @return <code>true</code> if the module type is supported, and
	 *    <code>false</code> otherwise
	 */
	public static boolean isSupportedModule(IModuleType[] moduleTypes, String typeId, String versionId) {
		if (moduleTypes == null)
			throw new IllegalArgumentException();
		
		if ("".equals(typeId))
			typeId = null;
		if ("".equals(versionId))
			versionId = null;
		
		if (typeId == null && versionId == null)
			return true;
		
		for (IModuleType moduleType : moduleTypes) {
			if (isSupportedModule(moduleType, typeId, versionId))
				return true;
		}
		
		return false;
	}

	private static boolean isSupportedModule(IModuleType[] moduleTypes, IModuleType[] mt) {
		if (mt != null) {
			for (IModuleType moduleType : mt) {
				if (isSupportedModule(moduleTypes, moduleType))
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if any of the given moduleTypes match the given
	 * module type.
	 * 
	 * @param moduleTypes an array of modules types, may not be null
	 * @param mt a module type, may not be null
	 * @return <code>true</code> if the module type is supported, and
	 *    <code>false</code> otherwise
	 */
	public static boolean isSupportedModule(IModuleType[] moduleTypes, IModuleType mt) {
		if (moduleTypes == null || mt == null)
			throw new IllegalArgumentException();
		
		for (IModuleType moduleType : moduleTypes) {
			if (isSupportedModule(moduleType, mt))
				return true;
		}
		return false;
	}

	private static boolean isSupportedModule(IModuleType moduleType, String type, String version) {
		String type2 = moduleType.getId();
		if (ServerPlugin.matches(type, type2)) {
			String version2 = moduleType.getVersion();
			if (ServerPlugin.matches(version, version2))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the two given module types are compatible.
	 * 
	 * @param moduleType a module type, may not be null
	 * @param mt a module type, may not be null
	 * @return <code>true</code> if the module type is supported, and
	 *    <code>false</code> otherwise
	 */
	public static boolean isSupportedModule(IModuleType moduleType, IModuleType mt) {
		if (moduleType == null || mt == null)
			throw new IllegalArgumentException();
		
		if (ServerPlugin.matches(mt.getId(), moduleType.getId()) &&
				ServerPlugin.matches(mt.getVersion(), moduleType.getVersion()))
			return true;
		
		return false;
	}

	/**
	 * Adds or removes modules from a server. Will search for the first parent module
	 * of each module and add it to the server instead. This method will handle multiple
	 * modules having the same parent (the parent will only be added once), but may not
	 * handle the case where the same module or parent is being both added and removed.
	 * Entries in the add or remove arrays may not be null.
	 * 
	 * @param server a server
	 * @param add an array of modules to add, or <code>null</code> to not add any
	 * @param remove an array of modules to remove, or <code>null</code> to not remove any
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if anything goes wrong
	 */
	public static void modifyModules(IServerWorkingCopy server, IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
		if (server == null)
			throw new IllegalArgumentException("Server cannot be null");
		
		if (add == null)
			add = new IModule[0];
		if (remove == null)
			remove = new IModule[0];
		
		for (IModule module : add) {
			if (module == null)
				throw new IllegalArgumentException("Cannot add null entries");
		}
		
		List<IModule> addParentModules = new ArrayList<IModule>();
		for (IModule module : add) {
			boolean found = false;
			try {
				IModule[] parents = server.getRootModules(module, monitor);
				if (parents != null && parents.length > 0) {				
					IModule parent = parents[0];
					if(parents.length > 1){
						for(int i = 1 ; i <parents.length; i++){
							if(module.equals(parents[i])){
								parent = parents[i];
								break;
							}
						}
					}
					found = true;
					if (!addParentModules.contains(parent))
						addParentModules.add(parent);
				}
			} catch (Exception e) {
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING, "Could not find parent module", e);
				}
			}
			
			if (!found)
				addParentModules.add(module);
		}
		
		for (IModule module : remove) {
			if (module == null)
				throw new IllegalArgumentException("Cannot remove null entries");
		}
		
		List<IModule> removeParentModules = new ArrayList<IModule>();
		for (IModule module : remove) {
			boolean found = false;
			try {
				IModule[] parents = server.getRootModules(module, monitor);
				if (parents != null && parents.length > 0) {				
					IModule parent = parents[0];
					found = true;
					if (!removeParentModules.contains(parent))
						removeParentModules.add(parent);
				}
			} catch (Exception e) {
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING, "Could not find parent module 2", e);
				}
			}
			
			if (!found)
				removeParentModules.add(module);
		}
		
		IModule[] add2 = new IModule[addParentModules.size()];
		addParentModules.toArray(add2);
		IModule[] remove2 = new IModule[removeParentModules.size()];
		removeParentModules.toArray(remove2);
		
		server.modifyModules(add2, remove2, monitor);
	}

	/**
	 * Sets a default name on the given runtime.
	 * 
	 * @param runtime a runtime
	 */
	public static void setRuntimeDefaultName(IRuntimeWorkingCopy runtime) {
		setRuntimeDefaultName(runtime, -1);
	}

	/**
	 * Sets a default name on the given runtime.
	 * 
	 * @param runtime
	 *          a runtime
	 * @param suffix
	 *          the numbering to start at for the suffix, if suffix is -1, then the no suffix name will be tried first.
	 * @return the suffix it found no name conflicts at and is using as part of
	 *         the default name. If suffix passed in was -1 and the no suffix name 
	 *         does not conflict, the suffix returned is -1
	 */
	public static int setRuntimeDefaultName(IRuntimeWorkingCopy runtime, int suffix) {
		String typeName = runtime.getRuntimeType().getName();

		String name = null;
		if (suffix == -1) {
			name = NLS.bind(Messages.defaultRuntimeName, typeName);
		} else {
			name = NLS.bind(Messages.defaultRuntimeName2, new String[] { typeName, suffix + "" });
		}

		if (ServerPlugin.isNameInUse(runtime.getOriginal(), name)){
			if (suffix == -1){
				// If the no suffix name is in use, the next suffix to try is 2
				suffix = 2;
			}
			else {
				suffix++;
			}
			
			name = NLS.bind(Messages.defaultRuntimeName2, new String[] { typeName, suffix + "" });
			while (ServerPlugin.isNameInUse(runtime.getOriginal(), name)) {
				suffix++;
				name = NLS.bind(Messages.defaultRuntimeName2, new String[] { typeName, suffix + "" });
			}
		}
		
		runtime.setName(name);
		return suffix;
	}

	/**
	 * Sets a default name on the given server.
	 * 
	 * @param server a server
	 */
	public static void setServerDefaultName(IServerWorkingCopy server) {
		if (server == null)
			throw new IllegalArgumentException();
		
		String typeName = server.getServerType().getName();
		String host = server.getHost();
		
		// base the name on the runtime if it exists and has been changed from the default
		IRuntime runtime = server.getRuntime();
		if (runtime != null && !(runtime instanceof RuntimeWorkingCopy)) {
			IRuntimeWorkingCopy wc = runtime.createWorkingCopy();
			setRuntimeDefaultName(wc);
			if (!wc.getName().equals(runtime.getName()))
				typeName = runtime.getName();
		}
		
		String name = NLS.bind(Messages.defaultServerName, new String[] {typeName, host});
		int i = 2;
		while (ServerPlugin.isNameInUse(server.getOriginal(), name)) {
			name = NLS.bind(Messages.defaultServerName2, new String[] {typeName, host, i + ""});
			i++;
		}
		server.setName(name);
	}

	private static boolean isValidFilename(String name) {
		IStatus status = ResourcesPlugin.getWorkspace().validateName(name, IResource.FILE);
		if (status != null && !status.isOK())
			return false;
		
		status = ResourcesPlugin.getWorkspace().validateName(name, IResource.FOLDER);
		if (status != null && !status.isOK())
			return false;
		
		return true;
	}

	private static String getValidFileName(String name) {
		if (isValidFilename(name))
			return name;
	
		// remove invalid characters
		String[] s = new String[] {".", "\\", "/", "?", ":", "*", "\"", "|", "<", ">"};
		int ind = 0;
		while (ind < s.length) {
			int index = name.indexOf(s[ind]);
			while (index >= 0) {
				name = name.substring(0, index) + name.substring(index+1);
				index = name.indexOf(s[ind]);
			}
			ind++;
		}
		return name;
	}

	/**
	 * Returns an unused file in the given project.
	 * 
	 * @param project a project
	 * @param server a server
	 * @return an unused file within the given project
	 */
	public static IFile getUnusedServerFile(IProject project, IServer server) {
		if (project == null || server == null)
			throw new IllegalArgumentException();
		
		String typeName = getValidFileName(server.getName());
		String name = NLS.bind(Messages.defaultServerName3, typeName)+ "."  + Server.FILE_EXTENSION;
		int i = 2;
		while (isFileNameInUse(project, name)) {
			name = NLS.bind(Messages.defaultServerName4, new String[] {typeName, i + ""}) + "."  + Server.FILE_EXTENSION;
			i++;
		}
		return project.getFile(name);
	}

	/**
	 * Returns true if an element exists with the given name.
	 *
	 * @param project a project
	 * @param name a file or folder name
	 * @return boolean <code>true</code> if the file or folder name is being
	 *    used, and <code>false</code> otherwise
	 */
	private static boolean isFileNameInUse(IProject project, String name) {
		if (name == null || project == null)
			return false;
		
		if (project.getFile(name).exists())
			return true;
		if (project.getFolder(name).exists())
			return true;
	
		return false;
	}

	/**
	 * Return a list of all runtime targets that match the given type and version.
	 * If type or version is null, it matches all of that type or version.
	 * 
	 * @param type a module type
	 * @param version a module version
	 * @return a possibly-empty array of runtime instances {@link IRuntime}
	 */
	public static IRuntime[] getRuntimes(String type, String version) {
		List<IRuntime> list = new ArrayList<IRuntime>();
		IRuntime[] runtimes = ServerCore.getRuntimes();
		if (runtimes != null) {
			for (IRuntime runtime : runtimes) {
				IRuntimeType runtimeType = runtime.getRuntimeType();
				if (runtimeType != null && isSupportedModule(runtimeType.getModuleTypes(), type, version)) {
					list.add(runtime);
				}
			}
		}
		
		IRuntime[] runtimes2 = new IRuntime[list.size()];
		list.toArray(runtimes2);
		return runtimes2;
	}

	/**
	 * Return a list of all runtime types that match the given type and version.
	 * If type or version is null, it matches all of that type or version.
	 * 
	 * @param type a module type
	 * @param version a module version
	 * @return a possibly-empty array of runtime type instances {@link IRuntimeType}
	 */
	public static IRuntimeType[] getRuntimeTypes(String type, String version) {
		List<IRuntimeType> list = new ArrayList<IRuntimeType>();
		IRuntimeType[] runtimeTypes = ServerCore.getRuntimeTypes();
		if (runtimeTypes != null) {
			for (IRuntimeType runtimeType : runtimeTypes) {
				if (isSupportedModule(runtimeType.getModuleTypes(), type, version)) {
					list.add(runtimeType);
				}
			}
		}
		
		IRuntimeType[] rt = new IRuntimeType[list.size()];
		list.toArray(rt);
		return rt;
	}
	
	/**
	 * Return a list of all runtime types that match the given type, version,
	 * and partial runtime type id. Multiple partial runtime type id are accepted 
	 * using a comma separated string. If type, version, or runtimeTypeId is null,
	 * it matches all of that type or version.
	 * 
	 * @param type a module type
	 * @param version a module version
	 * @param runtimeTypeId(s) the id of a runtime type. If multiple separate using comma
	 * @return a possibly-empty array of runtime type instances {@link IRuntimeType}
	 */
	public static IRuntimeType[] getRuntimeTypes(String type, String version, String runtimeTypeId) {
		List<IRuntimeType> list = new ArrayList<IRuntimeType>();
		IRuntimeType[] runtimeTypes = ServerCore.getRuntimeTypes();
		if (runtimeTypes != null) {
			for (IRuntimeType runtimeType : runtimeTypes) {
				if (isSupportedModule(runtimeType.getModuleTypes(), type, version)) {
					if (runtimeTypeId == null) {
						list.add(runtimeType);
					} else {
						StringTokenizer tokenizer = new StringTokenizer(runtimeTypeId, ",");
						while (tokenizer.hasMoreTokens()) {
							String curRuntimeTypeId = tokenizer.nextToken();
							if (runtimeType.getId().startsWith(curRuntimeTypeId)) {
								list.add(runtimeType);
							}
						}
					}
				}
			}
		}
		
		IRuntimeType[] rt = new IRuntimeType[list.size()];
		list.toArray(rt);
		return rt;
	}

	/**
	 * Returns a list of all servers that this module is not currently
	 * configured on, but could be added to. If includeErrors is true, this
	 * method return servers where the parent module may throw errors. For
	 * instance, this module may be the wrong spec level.
	 *
	 * @param module a module
	 * @param includeErrors <code>true</code> to include servers that returned
	 *    errors when trying to add the module, and <code>false</code> otherwise
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly empty array of servers
	 */
	public static IServer[] getAvailableServersForModule(IModule module, boolean includeErrors, IProgressMonitor monitor) {
		if (module == null)
			return new IServer[0];

		// do it the slow way - go through all servers and
		// see if this deployable is not configured in it
		// but could be added
		List<IServer> list = new ArrayList<IServer>();
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			for (IServer server : servers) { 
				if (!containsModule(server, module, monitor)) {
					try {
						IModule[] parents = server.getRootModules(module, monitor);
						if (parents != null && parents.length > 0) {
							boolean found = false;
							int size2 = parents.length;
							for (int j = 0; !found && j < size2; j++) {
								IModule parent = parents[j];
								IStatus status = server.canModifyModules(new IModule[] { parent }, new IModule[0], monitor);
								if (status == null || status.isOK()){
									list.add(server);
									found = true;
								}
							}
						}
					} catch (Exception se) {
						if (includeErrors)
							list.add(server);
					}
				}
			}
		}
		
		// make sure that the preferred server is the first one
		//IServer server = ServerCore.getServerPreferences().getDeployableServerPreference(deployable);
		//if (server != null && list.contains(server) && list.indexOf(server) != 0) {
		//	list.remove(server);
		//	list.add(0, server);
		//}

		IServer[] allServers = new IServer[list.size()];
		list.toArray(allServers);
		return allServers;
	}

	/**
	 * Returns a list of all servers that this module is configured on.
	 * 
	 * @param module a module
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly-empty array of server instances {@link IServer}
	 */
	public static IServer[] getServersByModule(IModule module, IProgressMonitor monitor) {
		if (module == null)
			return new IServer[0];

		// do it the slow way - go through all servers and
		// see if this module is configured in it
		List<IServer> list = new ArrayList<IServer>();
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			for (IServer server : servers) {
				if (containsModule(server, module, monitor))
					list.add(server);
			}
		}
		
		IServer[] allServers = new IServer[list.size()];
		list.toArray(allServers);
		return allServers;
	}

	/**
	 * Returns true if the given server currently contains the given module.
	 *
	 * @param server a server
	 * @param module a module
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return boolean <code>true</code> if the module is contained on the server,
	 *    or <code>false</code> otherwise
	 */
	public static boolean containsModule(IServer server, final IModule module, IProgressMonitor monitor) {
		if (server == null || module == null)
			throw new IllegalArgumentException("Arguments cannot be null");
		
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "containsModule() " + server + " " + module);
		}
		
		final boolean[] b = new boolean[1];
		
		((Server)server).visit(new IModuleVisitor() {
			public boolean visit(IModule[] modules) {
				int size = modules.length;
				if (modules[size - 1].equals(module)) {
					b[0] = true;
					return false;
				}
				return true;
			}
		}, null);
		return b[0];
	}

	/**
	 * Returns the server associated with the given launch configuration.
	 * 
	 * @param configuration a launch configuration
	 * @return the server associated with the launch configuration, or
	 *    <code>null</code> if no server could be found
	 * @throws CoreException if there is a problem getting the attribute from
	 *    the launch configuration
	 */
	public static IServer getServer(ILaunchConfiguration configuration) throws CoreException {
		String serverId = configuration.getAttribute(Server.ATTR_SERVER_ID, (String) null);

		if (serverId != null)
			return ServerCore.findServer(serverId);
		return null;
	}

	/**
	 * Validates whether this server can be editted.
	 * 
	 * @param context the context (Shell)
	 * @param server the server
	 * @return a status object with code <code>IStatus.OK</code> if the server
	 *   can be edited, otherwise a status object indicating what when wrong
	 *   with the checkout
	 */
	public static IStatus validateEdit(Object context, IServer server) {
		return ((Server)server).validateEdit(context);
	}

	/**
	 * Returns the port that is being used to monitor the given port on the server.
	 * This method can be used whenever creating a 'client' for the server, and allows
	 * the client to seamlessly use a monitored port instead of going directly to the
	 * server.
	 * 
	 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
	 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
	 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
	 * (repeatedly) as the API evolves.
	 * </p>
	 * 
	 * @param server a server
	 * @param port a port on the server
	 * @param contentType the content type, e.g. "web"
	 * @return the monitored port, or the original port number if the port is not
	 *    currently being monitored
	 */
	public static int getMonitoredPort(IServer server, int port, String contentType) {
		return ServerMonitorManager.getInstance().getMonitoredPort(server, port, contentType);
	}

	/**
	 * Returns a scheduling rule to prevent jobs from simultaneously starting,
	 * publishing, or stopping the same server.
	 * 
	 * @param server a server
	 * @return a scheduling rule for this server
	 * @since 2.0
	 * @deprecated the server instance is now a scheduling rule directly
	 */
	public static ISchedulingRule getServerSchedulingRule(IServer server) {
		return server;
	}

}