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

import org.eclipse.core.resources.*;
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
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public class ServerCore {
	//	cached copy of all runtime types
	private static List runtimeTypes;
	
	//	cached copy of all runtime target handlers
	private static List runtimeTargetHandlers;

	//	cached copy of all server and configuration types
	private static List serverTypes;

	static {
		executeStartups();
	}

	/**
	 * Cannot create ServerCore - use static methods.
	 */
	private ServerCore() {
		// can't create
	}

	/**
	 * Returns the resource manager.
	 *
	 * @return org.eclipse.wst.server.core.internal.ResourceManager
	 */
	private static ResourceManager getResourceManager() {
		return ResourceManager.getInstance();
	}
	
	/**
	 * Returns the preference information for the project. The project may not
	 * be null.
	 *
	 * @return org.eclipse.wst.server.core.IServerProjectPreferences
	 */
	public static IProjectProperties getProjectProperties(IProject project) {
		if (project == null)
			throw new IllegalArgumentException();
		return new ProjectProperties(project);
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
		if (runtimeTargetHandlers == null)
			loadRuntimeTargetHandlers();
		
		IRuntimeTargetHandler[] rth = new IRuntimeTargetHandler[runtimeTargetHandlers.size()];
		runtimeTargetHandlers.toArray(rth);
		return rth;
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
		if (id == null)
			throw new IllegalArgumentException();

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
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "runtimeTypes");

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
		sortOrderedList(runtimeTypes);
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .runtimeTypes extension point -<-");
	}
	
	/**
	 * Load the runtime target handlers.
	 */
	private static synchronized void loadRuntimeTargetHandlers() {
		if (runtimeTargetHandlers != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .runtimeTargetHandlers extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "runtimeTargetHandlers");

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
		sortOrderedList(runtimeTargetHandlers);
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .runtimeTargetHandlers extension point -<-");
	}

	/**
	 * Load the server types.
	 */
	private static synchronized void loadServerTypes() {
		if (serverTypes != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .serverTypes extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "serverTypes");

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
		sortOrderedList(serverTypes);
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .serverTypes extension point -<-");
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
	 * Sort the given list of IOrdered items into indexed order.
	 *
	 * @param list java.util.List
	 * @return java.util.List
	 */
	private static List sortOrderedList(List list) {
		if (list == null)
			return null;

		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				IOrdered a = (IOrdered) list.get(i);
				IOrdered b = (IOrdered) list.get(j);
				if (a.getOrder() > b.getOrder()) {
					Object temp = a;
					list.set(i, b);
					list.set(j, temp);
				}
			}
		}
		return list;
	}
}