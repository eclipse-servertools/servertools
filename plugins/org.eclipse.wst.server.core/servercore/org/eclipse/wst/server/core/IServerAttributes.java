/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
 * Restarting modules.
 * ]
 * </p>
 * <p>
 * [issue: The information actually stored in the (.server) file is:
 * server id and name, server type id, runtime id, server configuration id,
 * and test-environment. It's unclear what's gained by storing this
 * information in a workspace file. Is it so that this information
 * can be shared between users via a repository? Or is it just so that
 * there would be something to open in the resource navigator view?]
 * </p>
 * <p>
 * Two servers are identical if and only if they have the same id.
 * </p>
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IServerAttributes extends IAdaptable {
	/**
	 * File extension (value "server") for serialized representation of
	 * server instances.
	 * <p>
	 * [issue: What is relationship between this file extension and
	 * the file passed to IServerType.create(...) or returned by
	 * IServer.getFile()? That is, are server files expected to end
	 * in ".server", or is this just a default? If the former
	 * (as I suspect), then IServerType.create needs to say so,
	 * and the implementation should enforce the restriction.]
	 * </p>
	 */
	public static final String FILE_EXTENSION = "server";

	/**
	 * Server id attribute (value "server-id") of launch configurations.
	 * This attribute is used to tag a launch configuration with th
	 * id of the corresponding server.
	 * <p>
	 * [issue: This feels like an implementation detail. If it is to
	 * remain API, need to explain how a client uses this attribute.]
	 * </p>
	 * @see ILaunchConfiguration
	 */
	public static final String ATTR_SERVER_ID = "server-id";
	
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
	 * Returns true if this server is private (not shown in the UI).
	 * 
	 * @return <code>true</code> if this server is private,
	 *    and <code>false</code> otherwise
	 */
	public boolean isPrivate();

	/**
	 * Returns true if this is a working copy.
	 * 
	 * @return <code>true</code> if this server is a working copy,
	 *    and <code>false</code> otherwise
	 */
	public boolean isWorkingCopy();

	/**
	 * Returns true if the plugin containing the delegate is loaded.
	 * 
	 * @return boolean
	 */
	public boolean isDelegatePluginActivated();

	/**
	 * Returns true if the delegate has been loaded.
	 * 
	 * @return
	 */
	public boolean isDelegateLoaded();

	/**
	 * Validates whether this server can be editted.
	 * 
	 * @param context
	 * @return
	 */
	public IStatus validateEdit(Object context);

	/**
	 * Returns the timestamp of this server.
	 * Timestamps are monotonically increased each time the server is saved
	 * and can be used to determine if any changes have been made on disk
	 * since the server was loaded.
	 * 
	 * @return the server's timestamp
	 */
	public int getTimestamp();

	/**
	 * Returns the host for the server.
	 * The format of the host can be either a qualified or unqualified hostname,
	 * or an IP address and must conform to RFC 2732.
	 * 
	 * @return a host string conforming to RFC 2732
	 * @see java.net.URL.getHost()
	 */
	public String getHost();
	
	/**
	 * Returns the file where this server instance is serialized.
	 * 
	 * @return the file in the workspace where the server instance
	 * is serialized, or <code>null</code> if the information is
	 * instead to be persisted with the workspace but not with any
	 * particular workspace resource
	 */
	public IFile getFile();
	
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
	 * Returns a temporary directory that the requestor can use
	 * throughout it's lifecycle. This is primary to be used by
	 * servers for working directories, server specific
	 * files, etc.
	 *
	 * <p>As long as the same key is used to call this method on
	 * each use of the workbench, this method directory will return
	 * the same directory. If the directory is not requested over a
	 * period of time, the directory may be deleted and a new one
	 * will be assigned on the next request. For this reason, a
	 * server should request the temp directory on startup
	 * if it wants to store files there. In all cases, the server
	 * should have a backup plan to refill the directory
	 * in case it has been deleted since last use.</p>
	 *
	 * @return org.eclipse.core.runtime.IPath
	 */
	public IPath getTempDirectory();

	/**
	 * Returns an array of user modules that are currently being published to
	 * this server.
	 * <p>
	 * This method returns the root modules, which are not parented within
	 * another modules. Each of these may contain child modules, which are
	 * also deployed to this server.
	 * </p>
	 * 
	 * @see IServer.getModules()
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
	 * @param module org.eclipse.wst.server.core.IModule
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return array
	 */
	public IModule[] getChildModules(IModule module, IProgressMonitor monitor);

	/**
	 * Returns the parent module(s) of this module. When determining if a given
	 * project can run on a server, this method will be used to find the actual
	 * module(s) that may be run on the server. For instance, a Web module may
	 * return a list of EAR modules that it is contained in if the server only
	 * supports configuring EAR modules.
	 *
	 * <p>If the module type is not supported, this method will return null.
	 * If the type is normally supported but there is a configuration
	 * problem or missing parent, etc., this method will fire a CoreException
	 * that may then be presented to the user.</p>
	 *
	 * <p>If it does return valid parent(s), this method will always return
	 * the topmost parent module(s), even if there are a few levels
	 * (a heirarchy) of modules.</p>
	 *
	 * @param module org.eclipse.wst.server.core.IModule
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return an array of possible root modules
	 * @throws org.eclipse.core.runtime.CoreException
	 */
	public IModule[] getRootModules(IModule module, IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns an array of IServerPorts that this server has.
	 *
	 * @return the servers ports
	 */
	public IServerPort[] getServerPorts();
}