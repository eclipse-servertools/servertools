/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.server.core.model.IModuleEventsListener;
import org.eclipse.wst.server.core.model.IServerResourceListener;
/**
 * The resource manager handles the mappings between resources
 * and servers or configurations, and notifies of servers or configurations
 * being added, removed, or modified.
 * <p>
 * Servers and configurations may be a single resource, or they may
 * be a folder that contains a group of files. Folder resources may not
 * contain other servers or configurations (i.e., they cannot be nested).
 * </p>
 * <p>
 * Changes made to server element resources (e.g., an edit or deletion of a
 * file) are processed as a reload or deletion of the element. Note that saving
 * a folder-based server or configuration may result in a series of reload
 * events.
 * </p>
 * <p>
 * [issue: Not sure why "resource" is in the title. Runtimes
 * are not represented in the workspace. This grouping of things pertaining
 * to workspace resources is not particularly motivating to clients.]
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 1.0
 */
public interface IResourceManager {
	/**
	 * Adds a new server resource listener.
	 * Has no effect if an identical listener is already registered.
	 *
	 * @param listener org.eclipse.wst.server.model.IServerResourceListener
	 */
	public void addResourceListener(IServerResourceListener listener);

	/**
	 * Removes a server resource listener.
	 * Has no effect if the listener is not registered.
	 *
	 * @param listener org.eclipse.wst.server.model.IServerResourceListener
	 */
	public void removeResourceListener(IServerResourceListener listener);

	/**
	 * Returns an array of all known runtime instances. The list will not contain any
	 * working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of runtime instances {@link IRuntime}
	 */
	public IRuntime[] getRuntimes();
	
	/**
	 * Returns the default runtime. Test API - do not use.
	 * <p>
	 * [issue: This is marked "Test API - do not use."]
	 * </p>
	 *
	 * @return a runtime instance, or <code>null</code> if none
	 * @see #setDefaultRuntime(IRuntime)
	 */
	public IRuntime getDefaultRuntime();
	
	/**
	 * Sets the default runtime.
	 * <p>
	 * [issue: This is marked "Test API - do not use."]
	 * </p>
	 *
	 * @param runtime a runtime instance, or <code>null</code>
	 * @see #getDefaultRuntime()
	 */
	public void setDefaultRuntime(IRuntime runtime);
	
	/**
	 * Returns an array of all known runtime instances of
	 * the given runtime type. This convenience method filters the list of known
	 * runtime ({@link #getRuntimes()}) for ones with a matching
	 * runtime type ({@link IRuntime#getRuntimeType()}). The array will not
	 * contain any working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * <p>
	 * [issue: Is this convenience method really necessary?
	 * It's straightforward enough for a client to do.]
	 * </p>
	 * 
	 * @param runtimeType the runtime type
	 * @return a possibly-empty list of runtime instances {@link IRuntime}
	 * of the given runtime type
	 */
	public IRuntime[] getRuntimes(IRuntimeType runtimeType);
	
	/**
	 * Returns the runtime with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * runtimes ({@link #getRuntimes()}) for the one with a matching
	 * runtime id ({@link IRuntime#getId()}).
	 * <p>
	 * [issue: It does not really make sense for a key parameter
	 * like id to be null. 
	 * Null id should be spec'd as illegal, 
	 * and the implementation should immediately throw an unspecified 
	 * RuntimeException if null is passed.]
	 * </p>
	 * <p>
	 * [issue: Consider renaming this method findRuntime to make
	 * it clear that it is searching.]
	 * </p>
	 *
	 * @param the runtime id, or <code>null</code>
	 * @return the runtime instance, or <code>null</code> if 
	 * id is <code>null</code> or there is no runtime
	 * with the given id
	 */
	public IRuntime getRuntime(String id);

	/**
	 * Returns an array of all known server instances. The array will not include any
	 * working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of server instances {@link IServer}
	 */
	public IServer[] getServers();

	/**
	 * Returns the server with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * servers ({@link #getServers()}) for the one with a matching
	 * server id ({@link IServer#getId()}).
	 * <p>
	 * [issue: It does not really make sense for a key parameter
	 * like id to be null. 
	 * Null id should be spec'd as illegal, 
	 * and the implementation should immediately throw an unspecified 
	 * RuntimeException if null is passed.]
	 * </p>
	 * <p>
	 * [issue: Consider renaming this method findServer to make
	 * it clear that it is searching.]
	 * </p>
	 *
	 * @param the server id, or <code>null</code>
	 * @return the server instance, or <code>null</code> if 
	 * id is <code>null</code> or there is no server
	 * with the given id
	 */
	public IServer getServer(String id);

	/**
	 * Returns the server that came from the given file, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * servers ({@link #getServers()}) for the one with a matching
	 * location ({@link IServer#getFile()}).
	 * <p>
	 * [issue: Is this convenience method really necessary?
	 * It's straightforward enough for a client to do.]
	 * </p>
	 * <p>
	 * [issue: Consider renaming this method findServer to make
	 * it clear that it is searching.]
	 * </p>
	 *
	 * @param a server file
	 * @return the server instance, or <code>null</code> if 
	 * there is no server associated with the given file
	 */
	public IServer getServer(IFile file);
	
	/**
	 * Returns an array of all known server instances of
	 * the given server type. This convenience method filters the list of known
	 * servers ({@link #getServers()}) for ones with a matching
	 * server type ({@link IServer#getServerType()}). The array will not contain
	 * any working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * <p>
	 * [issue: Is this convenience method really necessary?
	 * It's straightforward enough for a client to do.]
	 * </p>
	 * 
	 * @param serverType the server type
	 * @return a possibly-empty array of server instances {@link IServer}
	 * of the given server type
	 */
	public IServer[] getServers(IServerType serverType);

	/**
	 * Returns an array of all known server configuration instances. The array will not
	 * include any working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of server configuration instances {@link IServerConfiguration}
	 */
	public IServerConfiguration[] getServerConfigurations();

	/**
	 * Returns an array of all known server configuration instances of
	 * the given server configuration type. This convenience method filters
	 * the list of known server configurations
	 * ({@link #getServerConfigurations()}) for ones with a matching
	 * server configuration type
	 * ({@link IServerConfiguration#getServerConfigurationType()}). The array will
	 * not contain any working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * <p>
	 * [issue: Is this convenience method really necessary?
	 * It's straightforward enough for a client to do.]
	 * </p>
	 * 
	 * @param configType the server configuration type
	 * @return a possibly-empty list of server configuration instances
	 * {@link IServerConfiguration) of the given server configuration type
	 */
	public IServerConfiguration[] getServerConfigurations(IServerConfigurationType configType);

	/**
	 * Returns the server configuration with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * server configurations ({@link #getServerConfigurations()}) for the one
	 * with a matching server configuration id
	 * ({@link IServerConfiguration#getId()}).
	 * <p>
	 * [issue: It does not really make sense for a key parameter
	 * like id to be null. 
	 * Null id should be spec'd as illegal, 
	 * and the implementation should immediately throw an unspecified 
	 * RuntimeException if null is passed.]
	 * </p>
	 * <p>
	 * [issue: Consider renaming this method findServerConfiguration to make
	 * it clear that it is searching.]
	 * </p>
	 *
	 * @param the server configuration id, or <code>null</code>
	 * @return the server configuration instance, or <code>null</code> if 
	 * id is <code>null</code> or there is no server configuration
	 * with the given id
	 */
	public IServerConfiguration getServerConfiguration(String id);

	/**
	 * Returns the server configuration that came from the given file, 
	 * or <code>null</code> if none. This convenience method searches the list
	 * of known server configurations ({@link #getServerConfigurations()}) for
	 * the one with a matching location ({@link IServerConfiguration#getFile()}).
	 * <p>
	 * [issue: Is this convenience method really necessary?
	 * It's straightforward enough for a client to do.]
	 * </p>
	 * <p>
	 * [issue: Consider renaming this method findServerConfiguration to make
	 * it clear that it is searching.]
	 * </p>
	 *
	 * @param a server configuration file
	 * @return the server configuration instance, or <code>null</code> if 
	 * there is no server configuration associated with the given file
	 */
	public IServerConfiguration getServerConfiguration(IFile file);

	/**
	 * Adds a new module events listener.
	 * Has no effect if an identical listener is already registered.
	 *
	 * @param listener org.eclipse.wst.server.model.IModuleEventsListener
	 */
	public void addModuleEventsListener(IModuleEventsListener listener);
	
	/**
	 * Removes an existing module events listener.
	 * Has no effect if the listener is not registered.
	 *
	 * @param listener org.eclipse.wst.server.model.IModuleEventsListener
	 */
	public void removeModuleEventsListener(IModuleEventsListener listener);
}