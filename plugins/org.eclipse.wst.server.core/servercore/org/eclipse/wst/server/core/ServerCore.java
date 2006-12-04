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
package org.eclipse.wst.server.core;

import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.internal.*;
/**
 * Main class for server core API.
 * <p>
 * This class provides API to access most of the types in the server
 * framework, including server runtimes and servers. Methods **
 * The methods on this class are thread safe.
 * </p>
 * <p>
 * This class provides all its functionality through static members.
 * It is not intended to be subclassed or instantiated.
 * </p>
 * 
 * @since 1.0
 */
public final class ServerCore {
	private static final String EXTENSION_SERVER_TYPE = "serverTypes";
	private static final String EXTENSION_RUNTIME_TYPE = "runtimeTypes";

	//	cached copy of all runtime types
	private static List runtimeTypes;

	//	cached copy of all server and configuration types
	private static List serverTypes;

	private static IRegistryChangeListener registryListener;

	static {
		executeStartups();
	}

	protected static class RegistryChangeListener implements IRegistryChangeListener {
		public void registryChanged(IRegistryChangeEvent event) {
			IExtensionDelta[] deltas = event.getExtensionDeltas(ServerPlugin.PLUGIN_ID, EXTENSION_RUNTIME_TYPE);
			if (deltas != null) {
				for (int i = 0; i < deltas.length; i++) {
					handleRuntimeTypeDelta(deltas[i]);
				}
			}
			
			deltas = event.getExtensionDeltas(ServerPlugin.PLUGIN_ID, EXTENSION_SERVER_TYPE);
			if (deltas != null) {
				for (int i = 0; i < deltas.length; i++) {
					handleServerTypeDelta(deltas[i]);
				}
			}
		}
	}

	/**
	 * Cannot instantiate ServerCore - use static methods.
	 */
	private ServerCore() {
		// can't create
	}

	/**
	 * Returns the resource manager.
	 *
	 * @return org.eclipse.wst.server.core.internal.ResourceManager
	 */
	private final static ResourceManager getResourceManager() {
		return ResourceManager.getInstance();
	}

	/**
	 * Returns the preference information for the project. The project may not
	 * be null.
	 *
	 * @param project a project
	 * @return the properties of the project
	 * @deprecated Project facet support should now be used instead of this API. @see
	 *    org.eclipse.wst.common.project.facet.core.IFacetedProject#getRuntime()
	 */
	public static IProjectProperties getProjectProperties(IProject project) {
		if (project == null)
			throw new IllegalArgumentException();
		return new IProjectProperties() {
			public IRuntime getRuntimeTarget() {
				return null;
			}
		};
	}

	/**
	 * Returns an array of all known runtime types.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return the array of runtime types {@link IRuntimeType}
	 */
	public static IRuntimeType[] getRuntimeTypes() {
		if (runtimeTypes == null)
			loadRuntimeTypes();
		
		IRuntimeType[] rt = new IRuntimeType[runtimeTypes.size()];
		runtimeTypes.toArray(rt);
		return rt;
	}

	/**
	 * Returns the runtime type with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * runtime types ({@link #getRuntimeTypes()}) for the one with a matching
	 * runtime type id ({@link IRuntimeType#getId()}). The id may not be null.
	 *
	 * @param id the runtime type id
	 * @return the runtime type, or <code>null</code> if there is no runtime type
	 * with the given id
	 */
	public static IRuntimeType findRuntimeType(String id) {
		if (id == null)
			throw new IllegalArgumentException();

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
	 * Returns an array of all known runtime target handler instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of runtime target handler instances
	 *    {@link IRuntimeTargetHandler}
	 */
	public static IRuntimeTargetHandler[] getRuntimeTargetHandlers() {
		throw new RuntimeException("Attempt to use deprecated code");
	}

	/**
	 * Returns the runtime target handler with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known runtime
	 * target handlers ({@link #getRuntimeTargetHandlers()}) for the one with
	 * a matching runtime target handler id ({@link IRuntimeTargetHandler#getId()}).
	 * The id may not be null.
	 *
	 * @param id the runtime target handler id
	 * @return the runtime target handler instance, or <code>null</code> if
	 *   there is no runtime target handler with the given id
	 */
	public static IRuntimeTargetHandler findRuntimeTargetHandler(String id) {
		throw new RuntimeException("Attempt to use deprecated code");
	}

	/**
	 * Returns an array of all known server types.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return the array of server types {@link IServerType}
	 */
	public static IServerType[] getServerTypes() {
		if (serverTypes == null)
			loadServerTypes();
		
		IServerType[] st = new IServerType[serverTypes.size()];
		serverTypes.toArray(st);
		return st;
	}

	/**
	 * Returns the server type with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * server types ({@link #getServerTypes()}) for the one with a matching
	 * server type id ({@link IServerType#getId()}). The id may not be null.
	 *
	 * @param id the server type id
	 * @return the server type, or <code>null</code> if there is no server type
	 * with the given id
	 */
	public static IServerType findServerType(String id) {
		if (id == null)
			throw new IllegalArgumentException();

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

	/**
	 * Load the runtime types.
	 */
	private static synchronized void loadRuntimeTypes() {
		if (runtimeTypes != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .runtimeTypes extension point ->-");
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, EXTENSION_RUNTIME_TYPE);
		List list = new ArrayList(cf.length);
		addRuntimeTypes(cf, list);
		addRegistryListener();
		runtimeTypes = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .runtimeTypes extension point -<-");
	}

	/**
	 * Load the runtime types.
	 */
	private static synchronized void addRuntimeTypes(IConfigurationElement[] cf, List list) {
		for (int i = 0; i < cf.length; i++) {
			try {
				list.add(new RuntimeType(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded runtimeType: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeType: " + cf[i].getAttribute("id"), t);
			}
		}
	}

	/**
	 * Load the server types.
	 */
	private static synchronized void loadServerTypes() {
		if (serverTypes != null)
			return;
		
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .serverTypes extension point ->-");
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, EXTENSION_SERVER_TYPE);
		List list = new ArrayList(cf.length);
		addServerTypes(cf, list);
		addRegistryListener();
		serverTypes = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .serverTypes extension point -<-");
	}

	/**
	 * Load the server types.
	 */
	private static synchronized void addServerTypes(IConfigurationElement[] cf, List list) {
		for (int i = 0; i < cf.length; i++) {
			try {
				list.add(new ServerType(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded serverType: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load serverType: " + cf[i].getAttribute("id"), t);
			}
		}
	}

	/**
	 * Returns the runtime with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * runtimes ({@link #getRuntimes()}) for the one with a matching
	 * runtime id ({@link IRuntime#getId()}). The id may not be null.
	 *
	 * @param id the runtime id
	 * @return the runtime instance, or <code>null</code> if there is no runtime
	 * with the given id
	 */
	public static IRuntime findRuntime(String id) {
		return getResourceManager().getRuntime(id);
	}

	/**
	 * Returns an array of all known runtime instances. The list will not contain any
	 * working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of runtime instances {@link IRuntime}
	 */
	public static IRuntime[] getRuntimes() {
		return getResourceManager().getRuntimes();
	}

	/**
	 * Returns the server with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * servers ({@link #getServers()}) for the one with a matching
	 * server id ({@link IServer#getId()}). The id must not be null.
	 *
	 * @param id the server id
	 * @return the server instance, or <code>null</code> if there is no server
	 * with the given id
	 */
	public static IServer findServer(String id) {
		return getResourceManager().getServer(id);
	}

	/**
	 * Returns an array of all known server instances. The array will not include any
	 * working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of server instances {@link IServer}
	 */
	public static IServer[] getServers() {
		return getResourceManager().getServers();
	}

	/**
	 * Adds a new runtime lifecycle listener.
	 * Has no effect if an identical listener is already registered.
	 *
	 * @param listener a runtime lifecycle listener
	 * @see #removeRuntimeLifecycleListener(IRuntimeLifecycleListener)
	 */
	public static void addRuntimeLifecycleListener(IRuntimeLifecycleListener listener) {
		getResourceManager().addRuntimeLifecycleListener(listener);
	}

	/**
	 * Removes a runtime lifecycle listener.
	 * Has no effect if the listener is not registered.
	 *
	 * @param listener a runtime lifecycle listener
	 * @see #addRuntimeLifecycleListener(IRuntimeLifecycleListener)
	 */
	public static void removeRuntimeLifecycleListener(IRuntimeLifecycleListener listener) {
		getResourceManager().removeRuntimeLifecycleListener(listener);
	}

	/**
	 * Adds a new server lifecycle listener.
	 * Has no effect if an identical listener is already registered.
	 *
	 * @param listener a server lifecycle listener
	 * @see #removeServerLifecycleListener(IServerLifecycleListener)
	 */
	public static void addServerLifecycleListener(IServerLifecycleListener listener) {
		getResourceManager().addServerLifecycleListener(listener);
	}

	/**
	 * Removes a server lifecycle listener.
	 * Has no effect if the listener is not registered.
	 *
	 * @param listener a server lifecycle listener
	 * #addServerLifecycleListener(IServerLifecycleListener)
	 */
	public static void removeServerLifecycleListener(IServerLifecycleListener listener) {
		getResourceManager().removeServerLifecycleListener(listener);
	}

	/**
	 * Returns the preferred runtime server for the given module. This method
	 * returns null if the server was never chosen or does not currently exist. (if the
	 * server is recreated or was in a closed project, etc. this method will return
	 * the original value if it becomes available again)
	 *
	 * @param module a module
	 * @return the current default server, or <code>null</code> if there is no
	 *    default server
	 */
	public static IServer getDefaultServer(IModule module) {
		return ModuleProperties.getInstance().getDefaultServer(module);
	}

	/**
	 * Sets the preferred runtime server for the given module. Set the server to
	 * null to clear the setting. If there is a problem saving the file, a CoreException
	 * will be thrown.
	 * 
	 * @param module the module to set the default for
	 * @param server the server to set the default server, or <code>null</code>
	 *    to unset the default
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is a problem setting the default server
	 */
	public static void setDefaultServer(IModule module, IServer server, IProgressMonitor monitor) throws CoreException {
		ModuleProperties.getInstance().setDefaultServer(module, server, monitor);
	}

	/**
	 * Handles a change to the server type extension point due to bundles getting added
	 * or removed dynamically at runtime.
	 * 
	 * @param delta an extension delta
	 */
	protected static void handleServerTypeDelta(IExtensionDelta delta) {
		if (serverTypes == null) // not loaded yet
			return;
		
		IConfigurationElement[] cf = delta.getExtension().getConfigurationElements();
		
		List list = new ArrayList(serverTypes);
		if (delta.getKind() == IExtensionDelta.ADDED) {
			addServerTypes(cf, list);
		} else {
			int size = list.size();
			ServerType[] st = new ServerType[size];
			list.toArray(st);
			int size2 = cf.length;
			
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size2; j++) {
					if (st[i].getId().equals(cf[j].getAttribute("id"))) {
						st[i].dispose();
						list.remove(st[i]);
					}
				}
			}
		}
		serverTypes = list;
		getResourceManager().resolveServers();
	}

	/**
	 * Handles a change to the runtime type extension point due to bundles getting added
	 * or removed dynamically at runtime.
	 * 
	 * @param delta an extension delta
	 */
	protected static void handleRuntimeTypeDelta(IExtensionDelta delta) {
		if (runtimeTypes == null) // not loaded yet
			return;
		
		IConfigurationElement[] cf = delta.getExtension().getConfigurationElements();
		
		List list = new ArrayList(runtimeTypes);
		if (delta.getKind() == IExtensionDelta.ADDED) {
			addRuntimeTypes(cf, list);
		} else {
			int size = list.size();
			RuntimeType[] rt = new RuntimeType[size];
			list.toArray(rt);
			int size2 = cf.length;
			
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size2; j++) {
					if (rt[i].getId().equals(cf[j].getAttribute("id"))) {
						rt[i].dispose();
						list.remove(rt[i]);
					}
				}
			}
		}
		runtimeTypes = list;
		getResourceManager().resolveRuntimes();
		getResourceManager().resolveServers();
	}

	private static void addRegistryListener() {
		if (registryListener != null)
			return;
		
		registryListener = new RegistryChangeListener();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		registry.addRegistryChangeListener(registryListener, ServerPlugin.PLUGIN_ID);
		ServerPlugin.setRegistryListener(registryListener);
	}
}