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
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
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
public interface IServer extends IElement, IAdaptable {
	
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
	 * Server state constant (value 0) indicating that the
	 * server is in an unknown state.
	 * 
	 * @see #getServerState()
	 * @see #getModuleState(IModule)
	 */
	public static final int STATE_UNKNOWN = 0;

	/**
	 * Server state constant (value 1) indicating that the
	 * server is starting, but not yet ready to serve content.
	 * 
	 * @see #getServerState()
	 * @see #getModuleState(IModule)
	 */
	public static final int STATE_STARTING = 1;

	/**
	 * Server state constant (value 2) indicating that the
	 * server is ready to serve content.
	 * 
	 * @see #getServerState()
	 * @see #getModuleState(IModule)
	 */
	public static final int STATE_STARTED = 2;

	/**
	 * Server state constant (value 3) indicating that the
	 * server is shutting down.
	 * 
	 * @see #getServerState()
	 * @see #getModuleState(IModule)
	 */
	public static final int STATE_STOPPING = 3;

	/**
	 * Server state constant (value 4) indicating that the
	 * server is stopped.
	 * 
	 * @see #getServerState()
	 * @see #getModuleState(IModule)
	 */
	public static final int STATE_STOPPED = 4;

	/**
	 * Publish state constant (value 0) indicating that it's
	 * in an unknown state.
	 * 
	 * @see #getServerPublishState()
	 * @see #getModulePublishState(IModule)
	 */
	public static final int PUBLISH_STATE_UNKNOWN = 0;

	/**
	 * Publish state constant (value 1) indicating that there
	 * is no publish required.
	 * 
	 * @see #getServerPublishState()
	 * @see #getModulePublishState(IModule)
	 */
	public static final int PUBLISH_STATE_NONE = 1;

	/**
	 * Publish state constant (value 2) indicating that an
	 * incremental publish is required.
	 * 
	 * @see #getServerPublishState()
	 * @see #getModulePublishState(IModule)
	 */
	public static final int PUBLISH_STATE_INCREMENTAL = 2;

	/**
	 * Publish state constant (value 1) indicating that a
	 * full publish is required.
	 * 
	 * @see #getServerPublishState()
	 * @see #getModulePublishState(IModule)
	 */
	public static final int PUBLISH_STATE_FULL = 3;

	/**
	 * Returns the current state of this server.
	 * <p>
	 * Note that this operation is guaranteed to be fast
	 * (it does not actually communicate with any actual
	 * server).
	 * </p>
	 *
	 * @return one of the server state (<code>SERVER_XXX</code>)
	 * constants declared on {@link IServer}
	 */
	public int getServerState();

	/**
	 * Returns the ILaunchManager mode that the server is in. This method will
	 * return null if the server is not running.
	 *  
	 * @return
	 */
	public String getMode();
	
	/**
	 * Returns the server's sync state.
	 *
	 * @return int
	 */
	public int getServerPublishState();
	
	/**
	 * Returns the module's sync state.
	 * @return
	 */
	public int getModulePublishState(IModule module);
	
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
	 * Returns an array of the modules that have not been published
	 * since the last modification. (i.e. the modules that are
	 * out of sync with the server.
	 *
	 * @return org.eclipse.wst.server.model.IModule[]
	 */
	public IModule[] getUnpublishedModules();

	/**
	 * Adds the given server state listener to this server.
	 * Once registered, a listener starts receiving notification of 
	 * state changes to this server. The listener continues to receive
	 * notifications until it is removed.
	 * Has no effect if an identical listener is already registered.
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
	 * Has no effect if an identical listener is already registered.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IPublishListener
	 */
	public void addPublishListener(IPublishListener listener);

	/**
	 * Removes a publish listener from this server.
	 * Has no effect if the listener is not registered.
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
	 * Publish to the server using the progress monitor. The result of the
	 * publish operation is returned as an IStatus.
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return status indicating what (if anything) went wrong
	 */
	public IStatus publish(IProgressMonitor monitor);

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
	public ILaunchConfiguration getLaunchConfiguration(boolean create, IProgressMonitor monitor) throws CoreException;

	public void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor);

	/**
	 * Asynchronously starts this server in the given launch mode.
	 * Returns the debug launch object that can be used in a debug
	 * session.
	 * <p>
	 * If canStart(launchMode) is false, this method will throw an
	 * exception.
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
	 * restarted. This method will return false when the
	 * server is not running.
	 * <p>
	 * [issue: Need to explain what is it that can get out of
	 * "out of sync" here, and how this can happen.]
	 * </p>
	 * 
	 * @return <code>true</code> if this server is out of sync and needs to be
	 * restarted, and <code>false</code> otherwise (e.g., if the contents have
	 * not been modified and the server process is still in sync); the
	 * result is unspecified if the server is not currently running
	 */
	public boolean getServerRestartState();

	/**
	 * Asynchronously restarts this server. This operation does
	 * nothing if this server cannot be stopped ({@link #canRestart()}
	 * returns <code>false</code>.
	 * This method cannot be used to start the server from a stopped state.
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
	 * Synchronously restarts this server. This operation does
	 * nothing if this server cannot be stopped ({@link #canRestart()}
	 * returns <code>false</code>.
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
	public void synchronousRestart(String launchMode, IProgressMonitor monitor) throws CoreException;

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
	 * Returns whether the given module can be restarted.
	 * <p>
	 * [issue: It's unclear whether this operations is guaranteed to be fast
	 * or whether it could involve communication with any actual
	 * server. If it is not fast, the method should take a progress
	 * monitor.]
	 * </p>
	 *
	 * @param module the module
	 * @return <code>true</code> if the given module can be
	 * restarted, and <code>false</code> otherwise 
	 */
	public boolean canRestartModule(IModule module);

	/**
	 * Check if the given module is in sync on the server. It should
	 * return true if the module should be restarted (is out of
	 * sync) or false if the module does not need to be restarted.
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return boolean
	 */
	public boolean getModuleRestartState(IModule module);

	/**
	 * Asynchronously restarts the given module on the server.
	 * See the specification of 
	 * {@link IServer#synchronousRestartModule(IModule, IProgressMonitor)}
	 * for further details. 
	 * <p>
	 * The implementation should update the module sync state and fire
	 * an event for the module. If the module does not exist on the server,
	 * an exception will be thrown.
	 * </p>
	 * <p>
	 * [issue: Since this method is ascynchronous, is there
	 * any need for the progress monitor?]
	 * </p>
	 * <p>
	 * [issue: Since this method is ascynchronous, how can
	 * it return a meaningful IStatus? 
	 * And IServer.synchronousModuleRestart throws CoreException
	 * if anything goes wrong.]
	 * </p>
	 * <p>
	 * [issue: If the module was just published to the server
	 * and had never been started, would is be ok to "start"
	 * the module using this method?]
	 * </p>
	 * 
	 * @param module the module to be started
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return status object
	 * @exception CoreException if an error occurs while trying to restart the module
	 */
	public void restartModule(IModule module, IProgressMonitor monitor) throws CoreException;

	/**
	 * Restarts the given module and waits until it has finished restarting.
	 * If the module does not exist on the server, an exception will be thrown.
	 * <p>
	 * This method may not be used to initially start a module.
	 * </p>
	 * 
	 * @param module the module to be restarted
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception CoreException if an error occurs while trying to restart the module
	 */
	public void synchronousRestartModule(IModule module, IProgressMonitor monitor) throws CoreException;

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
	 * Returns the current state of the given module on this server.
	 * Returns <code>STATE_UNKNOWN</code> if the module
	 * is not among the ones associated with this server.
	 *
	 * @param module the module
	 * @return one of the state (<code>STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 */
	public int getModuleState(IModule module);
	
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