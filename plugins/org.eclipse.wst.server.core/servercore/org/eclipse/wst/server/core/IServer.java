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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IModuleEvent;
import org.eclipse.wst.server.core.model.IModuleFactoryEvent;
import org.eclipse.wst.server.core.model.IPublishListener;
import org.eclipse.wst.server.core.model.IPublisher;
import org.eclipse.wst.server.core.model.IServerDelegate;
import org.eclipse.wst.server.core.model.IServerListener;
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
 * The resource manager maintains a global list of all known server instances
 * ({@link IResourceManager#getServers()}).
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
public interface IServer extends IElement {
	
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
	 * This attribute is used to tag a launch configuration with the
	 * id of the corresponding server.
	 * <p>
	 * [issue: This feels like an implementation detail. If it is to
	 * remain API, need to explain how a client uses this attribute.]
	 * </p>
	 * @see ILaunchConfiguration
	 */
	public static final String ATTR_SERVER_ID = "server-id";

	/**
	 * Server state constant (value 0) indicating that the
	 * server is in an unknown state.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getServerState()
	 */
	public static final byte SERVER_UNKNOWN = 0;

	/**
	 * Server state constant (value 1) indicating that the
	 * server is starting, but not yet ready to serve content.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getServerState()
	 */
	public static final byte SERVER_STARTING = 1;

	/**
	 * Server state constant (value 2) indicating that the
	 * server is ready to serve content.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getServerState()
	 */
	public static final byte SERVER_STARTED = 2;

	/**
	 * Server state constant (value 3) indicating that the
	 * server is started in debug mode and is ready to serve content.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * <p>
	 * [issue: SERVER_STARTED_DEBUG and SERVER_STARTED_PROFILE
	 * could be folded into SERVER_STARTED is there were 
	 * IServer.getMode() for querying what mode the server is running in.]
	 * </p>
	 * 
	 * @see #getServerState()
	 */
	public static final byte SERVER_STARTED_DEBUG = 3;
	
	/**
	 * Server state constant (value 4) indicating that the
	 * server is started in profiling mode and is ready to serve content.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * <p>
	 * [issue: SERVER_STARTED_DEBUG and SERVER_STARTED_PROFILE
	 * could be folded into SERVER_STARTED is there were 
	 * IServer.getMode() for querying what mode the server is running in.]
	 * </p>
	 * 
	 * @see #getServerState()
	 */
	public static final byte SERVER_STARTED_PROFILE = 4;

	/**
	 * Server state constant (value 5) indicating that the
	 * server is shutting down.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getServerState()
	 */
	public static final byte SERVER_STOPPING = 5;

	/**
	 * Server state constant (value 6) indicating that the
	 * server is stopped.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getServerState()
	 */
	public static final byte SERVER_STOPPED = 6;

	/**
	 * Server state constant (value 7) indicating that the
	 * server does not support getting the server state.
	 * <p>
	 * [issue: Given SERVER_UNKNOWN, is this state really
	 * necessary?]
	 * </p>
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getServerState()
	 */
	public static final byte SERVER_UNSUPPORTED = 7;


	/**
	 * Module state constant (value 0) indicating that the
	 * module is in an unknown state.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getModuleState(IModule)
	 */
	public static final byte MODULE_STATE_UNKNOWN = 0;

	/**
	 * Module state constant (value 1) indicating that the
	 * module is starting up, but not yet ready to serve its content.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getModuleState(IModule)
	 */
	public static final byte MODULE_STATE_STARTING = 1;

	/**
	 * Module state constant (value 2) indicating that the
	 * module is ready to serve its content.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getModuleState(IModule)
	 */
	public static final byte MODULE_STATE_STARTED = 2;

	/**
	 * Module state constant (value 3) indicating that the
	 * module is shutting down.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getModuleState(IModule)
	 */
	public static final byte MODULE_STATE_STOPPING = 3;

	/**
	 * Module state constant (value 4) indicating that the
	 * module is stopped.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * 
	 * @see #getModuleState(IModule)
	 */
	public static final byte MODULE_STATE_STOPPED = 4;


	// --- Sync State Constants ---
	// (returned from the isXxxInSnyc() methods)

	// the state of the server's contents are unknown
	public static final byte SYNC_STATE_UNKNOWN = 0;

	// the local contents exactly match the server's contents
	public static final byte SYNC_STATE_IN_SYNC = 1;

	// the local contents do not match the server's contents
	public static final byte SYNC_STATE_DIRTY = 2;
	
	/**
	 * Returns the current state of this server.
	 * <p>
	 * Note that this operation is guaranteed to be fast
	 * (it does not actually communicate with any actual
	 * server).
	 * </p>
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 *
	 * @return one of the server state (<code>SERVER_XXX</code>)
	 * constants declared on {@link IServer}
	 */
	public byte getServerState();
	
	/**
	 * Returns the host for the running server.
	 * The format of the host conforms to RFC 2732.
	 * <p>
	 * [issue: Consider renaming to "getHost" to bring in line
	 * with terminology used in java.net.URL. The host name can be
	 * either a host name or octets.]
	 * </p>
	 * 
	 * @return a host string conforming to RFC 2732
	 * @see java.net.URL.getHost()
	 */
	public String getHostname();
	
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
	 * Returns the server delegate for this server.
	 * The server delegate is a server-type-specific object.
	 * By casting the server delegate to the type prescribed in
	 * the API documentation for that particular server type, 
	 * the client can access server-type-specific properties and
	 * methods.
	 * <p>
	 * [issue: Exposing IServerDelegate to clients of IServer
	 * is confusing and dangerous. Instead, replace this
	 * method with something like getServerExtension() which
	 * returns an IServerExtension. IServerExtension is an
	 * "marker" interface that server providers would 
	 * implement or extend if they want to expose additional
	 * API for their server type. That way IServerDelegate
	 * can be kept entirely on the SPI side, out of view from 
	 * clients.]
	 * </p>
	 * <p>
	 * [issue: serverTypes schema, class attribute is optional.
	 * This suggests that a server need not provide a delegate class.
	 * This seems implausible. I've spec'd this method 
	 * as if delegate is mandatory.]
	 * </p>
	 * 
	 * @return the server delegate
	 */
	public IServerDelegate getDelegate();
	
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
	 * with this design.
	 * This method should be renamed "createWorkingCopy"
	 * or "newWorkingCopy" to make it clear to clients that it
	 * creates a new object, even for working copies.]
	 * </p>
	 * 
	 * @return a new working copy
	 */
	public IServerWorkingCopy getWorkingCopy();
	
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
	 * Returns the configuration's sync state.
	 *
	 * @return byte
	 */
	public byte getConfigurationSyncState();

	/**
	 * Returns a list of the projects that have not been published
	 * since the last modification. (i.e. the projects that are
	 * out of sync with the server.
	 *
	 * @return java.util.List
	 */
	public List getUnpublishedModules();

	/**
	 * Adds the given server state listener to this server.
	 * Once registered, a listener starts receiving notification of 
	 * state changes to this server. The listener continues to receive
	 * notifications until it is removed.
	 * <p>
	 * [issue: Duplicate server listeners should be ignored.]
	 * </p>
	 *
	 * @param listener the server listener
	 * @see #removeServerListener(IServerListener)
	 */
	public void addServerListener(IServerListener listener);
	
	/**
	 * Removes the given server state listener from this server. Has no
	 * effect if the listener is not registered.
	 * 
	 * @param listener the listener
	 * @see #addServerListener(IServerListener)
	 */
	public void removeServerListener(IServerListener listener);

	/**
	 * Adds a publish listener to this server.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IPublishListener
	 */
	public void addPublishListener(IPublishListener listener);
	
	/**
	 * Removes a publish listener from this server.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IPublishListener
	 */
	public void removePublishListener(IPublishListener listener);
	
	/**
	 * Returns whether this server is in a state that it can
	 * be published to.
	 *
	 * @return <code>true</code> if this server can be published to,
	 * and <code>false</code> otherwise
	 */
	public boolean canPublish();
	
	/**
	 * Returns true if the server may have any projects or it's
	 * configuration out of sync.
	 *
	 * @return boolean
	 */
	public boolean shouldPublish();
	
	/**
	 * Returns the publisher that can be used to publish the
	 * given module to this server.
	 * <p>
	 * [issue: It is unclear why the IPublisher is being exposed
	 * here at all. A normal client would call
	 * IServer.publish to instigate a publish operation; they would
	 * not be involved with anything finer-grained. Even the package
	 * suggests that IPublisher is something that should only be 
	 * relevant on the SPI side.]
	 * </p>
	 * <p>
	 * [issue: Explain the role of the parents parameter.]
	 * </p>
	 *
	 * @param parents the parent modules (element type: <code>IModule</code>)
	 * @param module the module
	 * @return the publisher that handles the given module, or
	 * <code>null</code> if the module cannot be published to
	 * this server
	 */
	public IPublisher getPublisher(List parents, IModule module);

	/**
	 * Publishes all associated modules to this server using
	 * the default publish manager.
	 * <p>
	 * [issue: The Server implementation of this method currently reads
	 * ServerCore.getPublishManager(ServerPreferences.DEFAULT_PUBLISH_MANAGER).
	 * This means that there is a fixed default (the smart publish manager).
	 * I think it should instead read
	 * ServerCore.getPublishManager(ServerPreferences.getDefaultPublishManager())
	 * which allows the default publish manager to be configured via 
	 * a preference.]
	 * </p>
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return status indicating what (if anything) went wrong
	 */
	public IStatus publish(IProgressMonitor monitor);
	
	/**
	 * Publishes all associated modules to this server using
	 * the given publish manager.
	 * 
	 * @param publishManager the publish manager that is to coordinate
	 * this publishing operation
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return status indicating what (if anything) went wrong
	 */
	public IStatus publish(IPublishManager publishManager, IProgressMonitor monitor);
	
	/**
	 * Returns whether this server is in a state that it can
	 * be started in the given mode.
	 *
	 * @param launchMode a mode in which a server can be launched,
	 * one of the mode constants defined by
	 * {@link org.eclipse.debug.core.ILaunchManager}
	 * @return <code>true</code> if this server can be started
	 * in the given mode, and <code>false</code> if it is either
	 * not ready to be started or if it does not support the given
	 * mode
	 */
	public boolean canStart(String launchMode);
	
	public ILaunch getExistingLaunch();
	
	/**
	 * Return the launch configuration for this server. If one does not exist, it
	 * will be created if "create" is true, and otherwise will return null.
	 * 
	 * @param create
	 * @return
	 * @throws CoreException
	 */
	public ILaunchConfiguration getLaunchConfiguration(boolean create) throws CoreException;

	public void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy);

	/**
	 * Asynchronously starts this server in the given launch mode.
	 * Returns the debug launch object that can be used in a debug
	 * session.
	 * <p>
	 * [issue: This method should specify what it does if
	 * canStart(launchMode) returns false.]
	 * </p>
	 * <p>
	 * [issue: There is no way to communicate failure to the
	 * client for the async portion of this operation. Given that
	 * this operation can go awry, there probably should be a mechanism
	 * that allows failing asynch operations to be diagnosed.]
	 * </p>
	 *
	 * @param launchMode a mode in which a server can be launched,
	 * one of the mode constants defined by
	 * {@link org.eclipse.debug.core.ILaunchManager}
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a debug launch object
	 * @exception CoreException if an error occurs while trying to start the server
	 */
	public ILaunch start(String launchMode, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Starts this server in the given launch mode and waits until the server
	 * has finished starting.
	 * <p>
	 * This convenience method uses {@link #start(String, IProgressMonitor)}
	 * to start the server, and an internal thread and listener to detect
	 * when the server has finished starting.
	 * </p>
	 * <p>
	 * [issue: Is there are particular reason why this method
	 * does not return the ILaunch that was used?]
	 * </p>
	 *
	 * @param launchMode a mode in which a server can be launched,
	 * one of the mode constants defined by
	 * {@link org.eclipse.debug.core.ILaunchManager}
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception CoreException if an error occurs while trying to start the server
	 */
	public void synchronousStart(String launchMode, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns whether this server is in a state that it can
	 * be restarted in the given mode. Note that only servers
	 * that are currently running can be restarted.
	 *
	 * @param launchMode a mode in which a server can be launched,
	 * one of the mode constants defined by
	 * {@link org.eclipse.debug.core.ILaunchManager}
	 * @return <code>true</code> if this server can be restarted
	 * in the given mode, and <code>false</code> if it is either
	 * not ready to be restarted or if it does not support the given
	 * mode
	 */
	public boolean canRestart(String mode);
	
	/**
	 * Returns whether this server is out of sync and needs to be
	 * restarted.
	 * <p>
	 * [issue: Need to explain what is it that can get out of
	 * "out of sync" here, and how this can happen.]
	 * </p>
	 * <p>
	 * [issue: Rather than have an unspecified result when the
	 * server is not running, this method should be spec'd to
	 * return false whenever canRestart() returns false.]
	 * </p>
	 * 
	 * @return <code>true</code> if this server is out of sync and needs to be
	 * restarted, and <code>false</code> otherwise (e.g., if the contents have
	 * not been modified and the server process is still in sync); the
	 * result is unspecified if the server is not currently running
	 */
	public boolean isRestartNeeded();

	/**
	 * Asynchronously restarts this server. This operation does
	 * nothing if this server cannot be stopped ({@link #canRestart()}
	 * returns <code>false</code>.
	 * <p>
	 * [issue: Lack of symmetry. Why is there no synchronousRestart? start and
	 * stop both have synchronous equivalents.]
	 * </p>
	 * <p>
	 * [issue: There is no way to communicate failure to the
	 * client. Given that this operation can go awry, there probably
	 * should be a mechanism that allows failing asynch operations
	 * to be diagnosed.]
	 * </p>
	 *
	 * @param launchMode a mode in which a server can be launched,
	 * one of the mode constants defined by
	 * {@link org.eclipse.debug.core.ILaunchManager}
	 */
	public void restart(String mode);

	/**
	 * Returns whether this server is in a state that it can
	 * be stopped.
	 * <p>
	 * [issue: Are there servers (or server types) that cannot be
	 * stopped? For instance, a server running on a remote host that 
	 * can be attached to, a published to, but neither started or
	 * stopped via this API. Or are we only talking about whether
	 * it is inconvenient to stop at this time?]
	 * </p>
	 *
	 * @return <code>true</code> if this server can be stopped,
	 * and <code>false</code> otherwise
	 */
	public boolean canStop();

	/**
	 * Asynchronously stops this server. This operation does
	 * nothing if this server cannot be stopped ({@link #canStop()}
	 * returns <code>false</code>.
	 * <p>
	 * [issue: There is no way to communicate failure to the
	 * client. Given that this operation can go awry, there probably
	 * should be a mechanism that allows failing asynch operations
	 * to be diagnosed.]
	 * </p>
	 */
	public void stop();

	/**
	 * Stops this server and waits until the server has completely stopped.
	 * <p>
	 * This convenience method uses {@link #stop()}
	 * to stop the server, and an internal thread and listener to detect
	 * when the server has complied.
	 * </p>
	 */
	public void synchronousStop();

	/**
	 * Terminates the server process(es). This method should only be
	 * used as a last resort after the stop() method fails to work.
	 * The server should return from this method quickly and
	 * use the server listener to notify shutdown progress.
	 * It MUST terminate the server completely and return it to
	 * the stopped state.
	 * <p>
	 * [issue: Since IServer already has stop(), it's hard to explain
	 * in what way this method is truely different. Given that stop()
	 * did not do the trick, why would terminate() have better luck.]
	 * </p>
	 */
	public void terminate();

	/**
	 * Restarts the given module and waits until it has finished restarting.
	 * <p>
	 * [issue: Lack of symmetry. Why is there no moduleRestart? start, restart,
	 * and stop all have assynchronous equivalents.]
	 * </p>
	 * <p>
	 * [issue: It should probably be spec'd to throw an exception error if the
	 * given module is not associated with the server.]
	 * </p>
	 * <p>
	 * [issue: This method should be renamed ""synchronousRestartModule".
	 * This would bring it into line with IServerDelegate.restartModule.]
	 * </p>
	 * <p>
	 * [issue: If the module was just published to the server
	 * and had never been started, would is be ok to "start"
	 * the module using this method?]
	 * </p>
	 * 
	 * @param module the module to be restarted
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception CoreException if an error occurs while trying to restart the module
	 */
	public void synchronousModuleRestart(IModule module, IProgressMonitor monitor) throws CoreException;

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
	 * @param serverResource org.eclipse.wst.server.core.model.IServerResource
	 * @return org.eclipse.core.runtime.IPath
	 */
	public IPath getTempDirectory();
	
	public void updateConfiguration();

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
	public IStatus canModifyModules(IModule[] add, IModule[] remove);

	/**
	 * Returns the list of modules that are associated with
	 * this server.
	 * <p>
	 * [issue: Clarify that these are root modules, not ones parented
	 * by some other module.]
	 * </p>
	 *
	 * @return a possibly-empty list of modules
	 */
	public IModule[] getModules();
	
	/**
	 * Returns the current state of the given module on this server.
	 * Returns <code>MODULE_STATE_UNKNOWN</code> if the module
	 * is not among the ones associated with this server.
	 * </p>
	 * <p>
	 * [issue: This operation gets forwarded to the delegate.
	 * It's unclear whether this operations is guaranteed to be fast
	 * or whether it could involve communication with any actual
	 * server. If it is not fast, the method should take a progress
	 * monitor.]
	 * </p>
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * <p>
	 * [issue: The SERVER_XXX and MODULE_STATE_XXX constants
	 * should be combined into a single set: {unknown, starting, started,
	 * stopping, stopped}.]
	 * </p>
	 *
	 * @param module the module
	 * @return one of the module state (<code>MODULE_STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 */
	public byte getModuleState(IModule module);
	
	/**
	 * Returns the child module(s) of this module. If this
	 * module contains other modules, it should list those
	 * modules. If not, it should return an empty list.
	 *
	 * <p>This method should only return the direct children.
	 * To obtain the full module tree, this method may be
	 * recursively called on the children.</p>
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return java.util.List
	 */
	public List getChildModules(IModule module);

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
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return java.util.List
	 * @throws org.eclipse.core.runtime.CoreException
	 */
	public List getParentModules(IModule module) throws CoreException;

	/**
	 * Method called when changes to the module or module factories
	 * within this configuration occur. Return any necessary commands to repair
	 * or modify the server configuration in response to these changes.
	 * 
	 * @param org.eclipse.wst.server.core.model.IModuleFactoryEvent[]
	 * @param org.eclipse.wst.server.core.model.IModuleEvent[]
	 * @return org.eclipse.wst.server.core.model.ITask[]
	 */
	public ITask[] getRepairCommands(IModuleFactoryEvent[] factoryEvent, IModuleEvent[] moduleEvent);
}
