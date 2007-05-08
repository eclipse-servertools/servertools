/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.*;
/**
 * Represents a server instance. Every server is an instance of a
 * particular, fixed server type.
 * <p>
 * Not surprisingly, the notion of <b>server</b> is central in the web tools
 * server infrastructure. In this context, understand that a server is
 * a web server of some ilk. It could be a simple web server lacking Java
 * support, or an J2EE based server, or perhaps even some kind of database
 * server. A more exact definition is not required for the purposes of this API.
 * From a tool-centric point of view, a server
 * is something that the developer is writing "content" for.
 * The unit of content is termed a module.
 * In a sense, the server exists, but lacks useful content. The
 * development task is to provide that content. The content can include
 * anything from simple, static HTML web pages to complex, highly dynamic
 * web applications.
 * In the course of writing and debugging this content,
 * the developer will want to test their content on a web server, to see how it
 * gets served up. For this they will need to launch a server process running on
 * some host machine (often the local host on which the IDE is running), or
 * attach to a server that's already running on a remote (or local) host. 
 * The newly developed content sitting in the developer's workspace needs to
 * end up in a location and format that the running server can use for its
 * serving purposes.
 * </p>
 * <p>
 * In this picture, an <code>IServer</code> object is a proxy for the real web
 * server. Through this proxy, a client can configure the server, and start,
 * stop, and restart it.
 * </p>
 * <p>
 * IServerAttributes implements IAdaptable to allow users to obtain a
 * server-type-specific class. By casting the runtime extension to the type
 * prescribed in the API documentation for that particular server type, the
 * client can access server-type-specific properties and methods.
 * getAdapter() may involve plugin loading, and should not be called from
 * popup menus, etc.
 * </p>
 * <p>
 * The server framework maintains a global list of all known server instances
 * ({@link ServerCore#getServers()}).
 * </p>
 * <p>
 * [rough notes:
 * Server has a state.
 * Server can be started, stopped, and restarted.
 * To modify server attributes, get a working copy, modify it, and then save it
 * to commit the changes.
 * Server attributes. Serialization.
 * Chained working copies for runtime, server configuration.
 * Server has a set of root modules.
 * Modules have state wrt a server.
 * Restarting modules.]
 * </p>
 * <p>
 * Two servers are identical if and only if they have the same id.
 * </p>
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @since 1.0
 */
public interface IServerAttributes extends IAdaptable {
	/**
	 * Returns the displayable name for this server.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name
	 */
	public String getName();
	
	/**
	 * Returns the id of this server.
	 * Each server (of a given type) has a distinct id, fixed for
	 * its lifetime. Ids are intended to be used internally as keys;
	 * they are not intended to be shown to end users.
	 * 
	 * @return the server id
	 */
	public String getId();

	/**
	 * Deletes the persistent representation of this server.
	 * 
	 * @throws CoreException if there was any error received while deleting the server
	 *    or if this method is called on a working copy
	 */
	public void delete() throws CoreException;

	/**
	 * Returns whether this server is marked read only.
	 * When a server is read only, working copies can be created but
	 * they cannot be saved.
	 *
	 * @return <code>true</code> if this server is marked as read only,
	 *    and <code>false</code> otherwise
	 */
	public boolean isReadOnly();

	/**
	 * Returns true if this is a working copy.
	 * 
	 * @return <code>true</code> if this server is a working copy,
	 *    and <code>false</code> otherwise
	 */
	public boolean isWorkingCopy();

	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> if
	 * no such object can be found, or if the delegate is not
	 * loaded.
	 * <p>
	 * This method will not check the delegate classes for adapting
	 * unless they are already loaded. No plugin loading will occur
	 * when calling this method. It is suitable for popup menus and
	 * other UI artifacts where performance is a concern.
	 * </p>
	 * 
	 * @param adapter the adapter class to look up
	 * @return a object castable to the given class, 
	 *    or <code>null</code> if this object does not
	 *    have an adapter for the given class
	 * @see IAdaptable#getAdapter(Class)
	 * @see #loadAdapter(Class, IProgressMonitor)
	 */
	public Object getAdapter(Class adapter);

	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> only if
	 * no such object can be found after loading and initializing
	 * delegates.
	 * <p>
	 * This method will force a load and initialization of all delegate
	 * classes and check them for adapting.
	 * </p>
	 * 
	 * @param adapter the adapter class to look up
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a object castable to the given class, 
	 *    or <code>null</code> if this object does not
	 *    have an adapter for the given class
	 * @see #getAdapter(Class)
	 */
	public Object loadAdapter(Class adapter, IProgressMonitor monitor);

	/**
	 * Returns the host for the server.
	 * The format of the host can be either a qualified or unqualified hostname,
	 * or an IP address and must conform to RFC 2732.
	 * 
	 * @return a host string conforming to RFC 2732
	 * @see java.net.URL#getHost()
	 */
	public String getHost();

	/**
	 * Returns the runtime associated with this server.
	 * <p>
	 * Note: The runtime of a server working copy may or may not
	 * be a working copy. For a server instance that is not a
	 * working copy, the runtime instance is not a working copy
	 * either.
	 * </p>
	 * <p>
	 * [issue: According to serverType extension point, 
	 * runtimeTypeId is a mandatory attribute. It seems odd
	 * then to have server runtime instance being an
	 * optional property of server instance. What does it mean
	 * for a server to not have a runtime?]
	 * </p>
	 * 
	 * @return the runtime, or <code>null</code> if none
	 */
	public IRuntime getRuntime();
	
	/**
	 * Returns the type of this server.
	 * 
	 * @return the server type
	 * @see IServerType
	 */
	public IServerType getServerType();
	
	/**
	 * Returns the server configuration associated with this server.
	 * <p>
	 * Note: The server configuration of a server working copy may
	 * or may not be a working copy. For a server instance that is
	 * not a working copy, the server configuration instance is not
	 * a working copy either.
	 * </p>
	 * <p>
	 * [issue: According to serverType extension point, 
	 * configurationTypeId is an optional attribute. If a server type
	 * has no server configuration type, then it seems reasonable to 
	 * expect this method to return null for all instances of that server
	 * type. But what about a server type that explicitly specifies
	 * a server configuration type. Does that mean that all server
	 * instances of that server type must have a server configuration
	 * instance of that server configuration type, and that this method
	 * never returns null in those cases?]
	 * </p>
	 * 
	 * @return the server configuration, or <code>null</code> if none
	 */
	public IFolder getServerConfiguration();

	/**
	 * Returns a server working copy for modifying this server instance.
	 * If this instance is already a working copy, it is returned.
	 * If this instance is not a working copy, a new server working copy
	 * is created with the same id and attributes.
	 * Clients are responsible for saving or releasing the working copy when
	 * they are done with it.
	 * <p>
	 * The server working copy is related to this server instance
	 * in the following ways:
	 * <pre>
	 * this.getWorkingCopy().getId() == this.getId()
	 * this.getWorkingCopy().getFile() == this.getFile()
	 * this.getWorkingCopy().getOriginal() == this
	 * this.getWorkingCopy().getRuntime() == this.getRuntime()
	 * this.getWorkingCopy().getServerConfiguration() == this.getServerConfiguration()
	 * </pre>
	 * </p>
	 * <p>
	 * [issue: IServerWorkingCopy extends IServer. 
	 * Server.getWorkingCopy() create a new working copy;
	 * ServerWorkingCopy.getWorkingCopy() returns this.
	 * This may be convenient in code that is ignorant of
	 * whether they are dealing with a working copy or not.
	 * However, it is hard for clients to manage working copies
	 * with this design.]
	 * </p>
	 * 
	 * @return a new working copy
	 */
	public IServerWorkingCopy createWorkingCopy();

	/**
	 * Returns an array of modules that are currently configured on
	 * the server. When the server is published, these are the modules
	 * that will be configured on the server. This method may not return
	 * the list of modules that are currently on the server if a module
	 * has been added or removed since the last publish.
	 * <p>
	 * This method returns the root modules, which are not parented within
	 * another modules. Each of these may contain child modules, which are
	 * also deployed to this server.
	 * </p>
	 * 
	 * @return a possibly-empty array of modules
	 */
	public IModule[] getModules();

	/**
	 * Returns whether the specified module modifications could be made to this
	 * server at this time.
	 * <p>
	 * This method may decide based on the type of module
	 * or refuse simply due to reaching a maximum number of
	 * modules or other criteria.
	 * </p>
	 * <p>
	 * [issue: This seems odd to have a pre-flight method.
	 * I should expect that the client can propose making
	 * any set of module changes they desire (via a server
	 * working copy). If the server doesn't like it, the operation
	 * should fail.]
	 * </p>
	 *
	 * @param add a possibly-empty list of modules to add
	 * @param remove a possibly-empty list of modules to remove
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return <code>true</code> if the proposed modifications
	 * look feasible, and <code>false</code> otherwise
	 */
	public IStatus canModifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor);

	/**
	 * Returns the child module(s) of this module. If this
	 * module contains other modules, it should list those
	 * modules. If not, it should return an empty list.
	 *
	 * <p>This method should only return the direct children.
	 * To obtain the full module tree, this method may be
	 * recursively called on the children.</p>
	 *
	 * @param module a module
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return an array of direct module children
	 */
	public IModule[] getChildModules(IModule module[], IProgressMonitor monitor);

	/**
	 * Returns the parent module(s) of this module. When determining if a given
	 * project can run on a server, this method will be used to find the actual
	 * module(s) that may be run on the server. For instance, a Web module may
	 * return a list of EAR modules that it is contained in if the server only
	 * supports configuring EAR modules. If the server supports running a module
	 * directly, the returned array should contain the module.
	 * 
	 * <p>If the module type is not supported, this method will return null or
	 * an empty array. If the type is normally supported but there is a
	 * configuration problem or missing parent, etc., this method will fire a
	 * CoreException that may then be presented to the user.</p>
	 * 
	 * <p>If it does return valid parent(s), this method will always return
	 * the topmost parent module(s), even if there are a few levels
	 * (a heirarchy) of modules.</p>
	 * 
	 * @param module a module
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return an array of possible root modules
	 * @throws CoreException if there is a problem
	 */
	public IModule[] getRootModules(IModule module, IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns an array of ServerPorts that this server has.
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly empty array of servers ports
	 */
	public ServerPort[] getServerPorts(IProgressMonitor monitor);
}