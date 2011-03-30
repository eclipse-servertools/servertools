/**********************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.internal.*;
/**
 * Main class for server core API.
 * <p>
 * This class provides API to access most of the types in the server
 * framework, including server runtimes and servers.
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
	private static List<IRuntimeType> runtimeTypes;

	//	cached copy of all server and configuration types
	private static List<IServerType> serverTypes;

	private static IRegistryChangeListener registryListener;

	protected static class RegistryChangeListener implements IRegistryChangeListener {
		public void registryChanged(IRegistryChangeEvent event) {
			IExtensionDelta[] deltas = event.getExtensionDeltas(ServerPlugin.PLUGIN_ID, EXTENSION_RUNTIME_TYPE);
			if (deltas != null) {
				for (IExtensionDelta delta : deltas)
					handleRuntimeTypeDelta(delta);
			}
			
			deltas = event.getExtensionDeltas(ServerPlugin.PLUGIN_ID, EXTENSION_SERVER_TYPE);
			if (deltas != null) {
				for (IExtensionDelta delta : deltas)
					handleServerTypeDelta(delta);
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
	 * @return the resource manager
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
	 * Load the runtime types.
	 */
	private static synchronized void loadRuntimeTypes() {
		if (runtimeTypes != null)
			return;
		if (Trace.EXTENSION_POINT) {
			Trace.trace(Trace.STRING_EXTENSION_POINT, "->- Loading .runtimeTypes extension point ->-");
		}
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, EXTENSION_RUNTIME_TYPE);
		List<IRuntimeType> list = new ArrayList<IRuntimeType>(cf.length);
		addRuntimeTypes(cf, list);
		addRegistryListener();
		runtimeTypes = list;
		
		if (Trace.EXTENSION_POINT) {
			Trace.trace(Trace.STRING_EXTENSION_POINT, "-<- Done loading .runtimeTypes extension point -<-");
		}
	}

	/**
	 * Load the runtime types.
	 */
	private static synchronized void addRuntimeTypes(IConfigurationElement[] cf, List<IRuntimeType> list) {
		for (IConfigurationElement ce : cf) {
			try {
				if (!ServerPlugin.contains(ServerPlugin.getExcludedServerAdapters(), ce.getAttribute("id")))
					list.add(new RuntimeType(ce));
				if (Trace.EXTENSION_POINT) {
					Trace.trace(Trace.STRING_EXTENSION_POINT, "  Loaded runtimeType: " + ce.getAttribute("id"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(
							Trace.STRING_SEVERE,
							"  Could not load runtimeType: "
									+ ce.getAttribute("id"), t);
				}
			}
		}
	}

	/**
	 * Load the server types.
	 */
	private static synchronized void loadServerTypes() {
		if (serverTypes != null)
			return;
		
		if (Trace.EXTENSION_POINT) {
			Trace.trace(Trace.STRING_EXTENSION_POINT, "->- Loading .serverTypes extension point ->-");
		}
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, EXTENSION_SERVER_TYPE);
		List<IServerType> list = new ArrayList<IServerType>(cf.length);
		addServerTypes(cf, list);
		addRegistryListener();
		serverTypes = list;
		
		if (Trace.EXTENSION_POINT) {
			Trace.trace(Trace.STRING_EXTENSION_POINT, "-<- Done loading .serverTypes extension point -<-");
		}
	}

	/**
	 * Load the server types.
	 */
	private static synchronized void addServerTypes(IConfigurationElement[] cf, List<IServerType> list) {
		for (IConfigurationElement ce : cf) {
			try {
				if (!ServerPlugin.contains(ServerPlugin.getExcludedServerAdapters(), ce.getAttribute("id")))
					list.add(new ServerType(ce));
				if (Trace.EXTENSION_POINT) {
					Trace.trace(Trace.STRING_EXTENSION_POINT, "  Loaded serverType: " + ce.getAttribute("id"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(
							Trace.STRING_SEVERE,
							"  Could not load serverType: "
									+ ce.getAttribute("id"), t);
				}
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
		
		List<IServerType> list = new ArrayList<IServerType>(serverTypes);
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
		
		List<IRuntimeType> list = new ArrayList<IRuntimeType>(runtimeTypes);
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

	/**
	 * Returns <code>true</code> if the preference is set to automatically
	 * publish when starting servers, or <code>false</code> otherwise
	 * 
	 * @return <code>true</code> if the preference is set to automatically
	 *    publish when starting servers, or <code>false</code> otherwise
	 * @since 1.1
	 */
	public static boolean isAutoPublishing() {
		return ServerPreferences.getInstance().isAutoPublishing();
	}

	public static boolean isPublishRequired(IServer server, IResourceDelta delta2) {
		PublishController[] controllers = ServerPlugin.getPublishController();
		if (controllers.length > 0){
			for (PublishController controller : controllers){
				if (server.getServerType() != null && controller.supportsType(server.getServerType().getId()))
					return controller.isPublishRequired(server, delta2);
			}
		}
		return true;
	}
}