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
 * [issue: Equality/identify for servers?]
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
public interface IServerAttributes extends IElement, IAdaptable {
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
	public IServerConfiguration getServerConfiguration();

	/**
	 * Returns the server extension for this server.
	 * The server extension is a server-type-specific object.
	 * By casting the server extension to the type prescribed in
	 * the API documentation for that particular server type, 
	 * the client can access server-type-specific properties and
	 * methods.
	 * 
	 * @return the server extension
	 */
	//public IServerExtension getExtension(IProgressMonitor monitor);

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
	 * Returns whether the given server configuration can be used with
	 * this server.
	 * <p>
	 * [issue: This seems to be just a convenience method. Given that it's 
	 * straightforward enought for a client to compare 
	 * this.getServerType().getServerConfiguration()
	 * to configuration.getServerConfigurationType(),
	 * it's not clear that there is a great need for this method.]
	 * </p>
	 * <p>
	 * [issue: It does not make sense to allow a null configuration.]
	 * </p>
	 * 
	 * Returns true if this is a configuration that is
	 * applicable to (can be used with) this server.
	 *
	 * @param configuration the server configuration
	 * @return <code>true</code> if this server supports the given server
	 * configuration, and <code>false/code> otherwise
	 */
	public boolean isSupportedConfiguration(IServerConfiguration configuration);

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
	 * Returns an array of modules that are associated with
	 * this server.
	 * <p>
	 * [issue: Clarify that these are root modules, not ones parented
	 * by some other module.]
	 * </p>
	 *
	 * @return a possibly-empty array of modules
	 */
	public IModule[] getModules(IProgressMonitor monitor);
	
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
	 * @return array
	 */
	public IModule[] getChildModules(IModule module, IProgressMonitor monitor);

	/**
	 * Returns the parent module(s) of this module. When
	 * determining if a given project can run on a server
	 * configuration, this method will be used to find the
	 * actual module(s) that may be run on the server. For
	 * instance, a Web module may return a list of Ear
	 * modules that it is contained in if the server only
	 * supports configuring Ear modules.
	 *
	 * <p>If the module type is not supported, this method
	 * may return null. If the type is normally supported but there
	 * is a configuration problem or missing parent, etc., this
	 * method may fire a CoreException that may then be presented
	 * to the user.</p>
	 *
	 * <p>If it does return valid parent(s), this method should
	 * always return the topmost parent module(s), even if
	 * there are a few levels (a heirarchy) of modules.</p>
	 *
	 * @param module org.eclipse.wst.server.core.IModule
	 * @return array
	 * @throws org.eclipse.core.runtime.CoreException
	 */
	public IModule[] getParentModules(IModule module, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns an array of IServerPorts that this server has.
	 *
	 * @return
	 */
	public IServerPort[] getServerPorts();
}