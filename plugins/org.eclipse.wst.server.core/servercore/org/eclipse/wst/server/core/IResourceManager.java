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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.server.core.model.*;
/**
 * IResourceManager handles the mappings between resources
 * and servers or configurations, and creates notification
 * of servers or configurations being added, removed, or modified.
 *
 * <p>Servers and configurations may be a single resource, or they may
 * be a folder that contains a group of files.Folder-resources may not
 * contain other servers or configurations. (i.e. they cannot be nested)</p>
 *
 * <p>Changes made to server element resources (e.g. an edit or deletion of a
 * file) are processed as a reload or deletion of the element. Note that saving
 * a folder-based server or configuration may result in a series of reload
 * events.</p>
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IResourceManager {
	/**
	 * Adds a new server resource listener.
	 *
	 * @param listener org.eclipse.wst.server.model.IServerResourceListener
	 */
	public void addResourceListener(IServerResourceListener listener);

	/**
	 * Removes a server resource listener.
	 *
	 * @param listener org.eclipse.wst.server.model.IServerResourceListener
	 */
	public void removeResourceListener(IServerResourceListener listener);

	/**
	 * Returns a list of all runtimes.
	 *
	 * @return java.util.List
	 */
	public List getRuntimes();
	
	/**
	 * Returns the default runtime. Test API - do not use.
	 *
	 * @return java.util.List
	 */
	public IRuntime getDefaultRuntime();
	
	/**
	 * Sets the default runtime. Test API - do not use.
	 *
	 * @return java.util.List
	 */
	public void setDefaultRuntime(IRuntime runtime);
	
	/**
	 * Returns the runtimes with the given runtime type.
	 *
	 * @return java.util.List
	 */
	public List getRuntimes(IRuntimeType runtimeType);
	
	/**
	 * Returns the runtime with the given id.
	 *
	 * @return IRuntime
	 */
	public IRuntime getRuntime(String id);

	/**
	 * Returns a list of all servers.
	 *
	 * @return java.util.List
	 */
	public List getServers();

	/**
	 * Returns the server with the given id.
	 *
	 * @return java.util.List
	 */
	public IServer getServer(String id);

	/**
	 * Returns the server that came from the given file.
	 *
	 * @param file org.eclipse.core.resources.IFile
	 * @return org.eclipse.wst.server.model.IServer
	 */
	public IServer getServer(IFile file);
	
	/**
	 * Returns a list of all servers.
	 *
	 * @return java.util.List
	 */
	public List getServers(IServerType serverType);

	/**
	 * Returns a list of all currently active server configurations.
	 *
	 * @return java.util.List
	 */
	public List getServerConfigurations();
	
	/**
	 * Returns a list of all currently active server configurations.
	 *
	 * @return java.util.List
	 */
	public List getServerConfigurations(IServerConfigurationType configType);

	/**
	 * Returns the servers with the given id.
	 *
	 * @return java.util.List
	 */
	public IServerConfiguration getServerConfiguration(String id);

	/**
	 * Returns the server configuration that came from the
	 * given resource.
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 * @return org.eclipse.wst.server.model.IServerConfiguration
	 */
	public IServerConfiguration getServerConfiguration(IFile file);

	/**
	 * Adds a new module events listener.
	 *
	 * @param listener org.eclipse.wst.server.model.IModuleEventsListener
	 */
	public void addModuleEventsListener(IModuleEventsListener listener);
	
	/**
	 * Removes an existing module events listener.
	 *
	 * @param listener org.eclipse.wst.server.model.IModuleEventsListener
	 */
	public void removeModuleEventsListener(IModuleEventsListener listener);

	/**
	 * Adds a new server lifecycle event handler with the given index. Handlers with
	 * lower indexes are always called first.
	 *
	 * @param handler org.eclipse.wst.server.model.IServerLifecycleEventHandler
	 */
	public void addServerLifecycleEventHandler(int index, IServerLifecycleEventHandler handler);

	/**
	 * Removes an existing server lifecycle event handler.
	 *
	 * @param handler org.eclipse.wst.server.model.IServerLifecycleEventHandler
	 */
	public void removeServerLifecycleEventHandler(IServerLifecycleEventHandler handler);
}