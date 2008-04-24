/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;

import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
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
 * Two servers are identical if and only if they have the same id.
 * </p>
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @since 1.0
 */
public interface IServer extends IServerAttributes, ISchedulingRule {
	/**
	 * An operation listener is used to receive notification back about a
	 * specific server operation, such as starting or stopping a server.
	 * 
	 * @since 1.0
	 */
	public interface IOperationListener {
		/**
		 * Called once when the operation is complete.
		 * 
		 * @param result a status object with code <code>IStatus.OK</code> if
		 *    the operation completed successfully, otherwise a status object
		 *    indicating why it didn't
		 */
		public void done(IStatus result);
	}

	/**
	 * Server state constant (value 0) indicating that the
	 * server is in an unknown state.
	 * 
	 * @see #getServerState()
	 * @see #getModuleState(IModule[])
	 */
	public static final int STATE_UNKNOWN = 0;

	/**
	 * Server state constant (value 1) indicating that the
	 * server is starting, but not yet ready to serve content.
	 * 
	 * @see #getServerState()
	 * @see #getModuleState(IModule[])
	 */
	public static final int STATE_STARTING = 1;

	/**
	 * Server state constant (value 2) indicating that the
	 * server is ready to serve content.
	 * 
	 * @see #getServerState()
	 * @see #getModuleState(IModule[])
	 */
	public static final int STATE_STARTED = 2;

	/**
	 * Server state constant (value 3) indicating that the
	 * server is shutting down.
	 * 
	 * @see #getServerState()
	 * @see #getModuleState(IModule[])
	 */
	public static final int STATE_STOPPING = 3;

	/**
	 * Server state constant (value 4) indicating that the
	 * server is stopped.
	 * 
	 * @see #getServerState()
	 * @see #getModuleState(IModule[])
	 */
	public static final int STATE_STOPPED = 4;

	/**
	 * Publish state constant (value 0) indicating that it's
	 * in an unknown state.
	 * 
	 * @see #getServerPublishState()
	 * @see #getModulePublishState(IModule[])
	 */
	public static final int PUBLISH_STATE_UNKNOWN = 0;

	/**
	 * Publish state constant (value 1) indicating that there
	 * is no publish required.
	 * 
	 * @see #getServerPublishState()
	 * @see #getModulePublishState(IModule[])
	 */
	public static final int PUBLISH_STATE_NONE = 1;

	/**
	 * Publish state constant (value 2) indicating that an
	 * incremental publish is required.
	 * 
	 * @see #getServerPublishState()
	 * @see #getModulePublishState(IModule[])
	 */
	public static final int PUBLISH_STATE_INCREMENTAL = 2;

	/**
	 * Publish state constant (value 3) indicating that a
	 * full publish is required.
	 * 
	 * @see #getServerPublishState()
	 * @see #getModulePublishState(IModule[])
	 */
	public static final int PUBLISH_STATE_FULL = 3;

	/**
	 * Publish kind constant (value 1) indicating an incremental publish request.
	 * 
	 * @see #publish(int, IProgressMonitor)
	 */
	public static final int PUBLISH_INCREMENTAL = 1;

	/**
	 * Publish kind constant (value 2) indicating a full publish request.
	 * 
	 * @see #publish(int, IProgressMonitor)
	 */
	public static final int PUBLISH_FULL = 2;

	/**
	 * Publish kind constant (value 3) indicating an automatic publish request.
	 * 
	 * @see #publish(int, IProgressMonitor)
	 */
	public static final int PUBLISH_AUTO = 3;

	/**
	 * Publish kind constant (value 4) indicating a publish clean request
	 * 
	 * @see #publish(int, IProgressMonitor)
	 */
	public static final int PUBLISH_CLEAN = 4;

	/**
	 * Returns the current state of this server.
	 * <p>
	 * Note that this operation is guaranteed to be fast
	 * (it does not actually communicate with any actual
	 * server).
	 * </p>
	 *
	 * @return one of the server state (<code>STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 */
	public int getServerState();

	/**
	 * Returns the ILaunchManager mode that the server is in. This method will
	 * return null if the server is not running.
	 * 
	 * @return the mode in which a server is running, one of the mode constants
	 *    defined by {@link org.eclipse.debug.core.ILaunchManager}, or
	 *    <code>null</code> if the server is stopped.
	 */
	public String getMode();

	/**
	 * Returns the server's sync state.
	 *
	 * @return one of the PUBLISH_XXX state flags
	 */
	public int getServerPublishState();

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
	 * Adds the given server state listener to this server.
	 * Once registered, a listener starts receiving notification of 
	 * state changes to this server. The listener continues to receive
	 * notifications until it is removed.
	 * Has no effect if an identical listener is already registered.
	 *
	 * @param listener the server listener
	 * @param eventMask the bit-wise OR of all event types of interest to the
	 * listener
	 * @see #removeServerListener(IServerListener)
	 */
	public void addServerListener(IServerListener listener, int eventMask);

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
	 * @param listener the publish listener
	 * @see #removePublishListener(IPublishListener)
	 */
	public void addPublishListener(IPublishListener listener);

	/**
	 * Removes a publish listener from this server.
	 * Has no effect if the listener is not registered.
	 *
	 * @param listener the publish listener
	 * @see #addPublishListener(IPublishListener)
	 */
	public void removePublishListener(IPublishListener listener);

	/**
	 * Returns whether this server is in a state that it can
	 * be published to.
	 *
	 * @return a status object with code <code>IStatus.OK</code> if the server can
	 *   be published to, otherwise a status object indicating what is wrong
	 */
	public IStatus canPublish();

	/**
	 * Returns true if the server should be published to. This is <code>true</code> when the server
	 * can be published to and the server's publish state or any module's publish state is not
	 * PUBLISH_STATE_NONE. 
	 * 
	 * @return boolean
	 * @since 2.0
	 */
	public boolean shouldPublish();

	/**
	 * Publish to the server using the progress monitor. The result of the
	 * publish operation is returned as an IStatus.
	 * <p>
	 * This method should not be called from the UI thread. Publishing is long-
	 * running and may trigger resource change events or builds. Although this
	 * framework is safe, there is no guarantee that other bundles are UI-safe
	 * and the risk of UI deadlock is high. 
	 * </p>
	 * 
	 * @param kind the kind of publish being requested. Valid values are:
	 *    <ul>
	 *    <li><code>PUBLISH_FULL</code>- indicates a full publish.</li>
	 *    <li><code>PUBLISH_INCREMENTAL</code>- indicates a incremental publish.
	 *    <li><code>PUBLISH_CLEAN</code>- indicates a clean request. Clean throws
	 *      out all state and cleans up the module on the server before doing a
	 *      full publish.
	 *    </ul>
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return status indicating what (if anything) went wrong
	 * @see #publish(int, List, IAdaptable, IOperationListener)
	 */
	public IStatus publish(int kind, IProgressMonitor monitor);

	/**
	 * Publish one or more modules to the server.
	 * <p>
	 * The operation listener can be used to add a listener for notification
	 * of the publish result. The listener will be called with a
	 * single successful status (severity OK) when the server has
	 * finished publishing, or a single failure (severity ERROR) if
	 * there was an error publishing to the server.
	 * </p><p>
	 * This method should not be called from the UI thread. Publishing is long-
	 * running and may trigger resource change events or builds. Although this
	 * framework is safe, there is no guarantee that other bundles are UI-safe
	 * and the risk of UI deadlock is high. 
	 * </p>
	 * 
	 * @param kind the kind of publish being requested. Valid values are:
	 *    <ul>
	 *    <li><code>PUBLISH_FULL</code>- indicates a full publish.</li>
	 *    <li><code>PUBLISH_INCREMENTAL</code>- indicates a incremental publish.
	 *    <li><code>PUBLISH_CLEAN</code>- indicates a clean request. Clean throws
	 *      out all state and cleans up the module on the server before doing a
	 *      full publish.
	 *    </ul>
	 * @param modules a list of modules to publish, or <code>null</code> to
	 *    publish all modules
	 * @param info the IAdaptable (or <code>null</code>) provided by the
	 *    caller in order to supply UI information for prompting the
	 *    user if necessary. When this parameter is not
	 *    <code>null</code>, it should minimally contain an adapter
	 *    for the Shell class.
	 * @param listener an operation listener to receive notification when this
	 *    operation is done, or <code>null</code> if notification is not
	 *    required
	 * @since 3.0
	 */
	public void publish(int kind, List<IModule[]> modules, IAdaptable info, IOperationListener listener);

	/**
	 * Returns whether this server is in a state that it can
	 * be started in the given mode.
	 *
	 * @param launchMode a mode in which a server can be launched,
	 *    one of the mode constants defined by
	 *    {@link org.eclipse.debug.core.ILaunchManager}
	 * @return a status object with code <code>IStatus.OK</code> if the server can
	 *    be started, otherwise a status object indicating why it can't
	 */
	public IStatus canStart(String launchMode);

	/**
	 * Asynchronously starts this server in the given launch mode.
	 * <p>
	 * If canStart(launchMode) is false, this method will throw an
	 * exception.
	 * </p>
	 * <p>
	 * If the caller wants to listen for failure or success of the
	 * server starting, it can add a server listener or use the
	 * version of this method that takes an operation listener as a
	 * parameter.
	 * </p>
	 *
	 * @param launchMode a mode in which a server can be launched,
	 *    one of the mode constants defined by
	 *    {@link org.eclipse.debug.core.ILaunchManager}
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception CoreException if an error occurs while trying to start the server
	 * @see #start(String, IServer.IOperationListener)
	 */
	public void start(String launchMode, IProgressMonitor monitor) throws CoreException;

	/**
	 * Asynchronously starts this server in the given launch mode.
	 * <p>
	 * If canStart(launchMode) is false, this method will throw an
	 * exception.
	 * </p>
	 * <p>
	 * The operation listener can be used to add a listener for notification
	 * of this specific server launch. The listener will be called with a
	 * single successful status (severity OK) when the server has
	 * finished starting, or a single failure (severity ERROR) if
	 * there was an error starting the server.
	 * </p>
	 * 
	 * @param launchMode a mode in which a server can be launched,
	 *    one of the mode constants defined by
	 *    {@link org.eclipse.debug.core.ILaunchManager}
	 * @param listener an operation listener to receive notification when this
	 *    operation is done, or <code>null</code> if notification is not
	 *    required
	 */
	public void start(String launchMode, IOperationListener listener);

	/**
	 * Returns whether this server is in a state that it can
	 * be restarted in the given mode. Note that only servers
	 * that are currently running can be restarted.
	 *
	 * @param mode a mode in which a server can be launched,
	 *    one of the mode constants defined by
	 *    {@link org.eclipse.debug.core.ILaunchManager}
	 * @return a status object with code <code>IStatus.OK</code> if the server can
	 *    be restarted, otherwise a status object indicating why it can't
	 */
	public IStatus canRestart(String mode);

	/**
	 * Returns true if the server should be restarted. This is <code>true</code> when the server
	 * can be restarted and the server's restart state or any module's restart states is not
	 * false. 
	 * 
	 * @return boolean
	 * @since 2.0
	 */
	public boolean shouldRestart();

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
	 * nothing if this server cannot be stopped ({@link #canRestart(String)}
	 * returns <code>false</code>.
	 * This method cannot be used to start the server from a stopped state.
	 * <p>
	 * If the caller wants to listen for failure or success of the
	 * server restarting, it can add a server listener or use the
	 * version of this method that takes an operation listener as a
	 * parameter.
	 * </p>
	 *
	 * @param launchMode a mode in which a server can be launched,
	 *    one of the mode constants defined by
	 *    {@link org.eclipse.debug.core.ILaunchManager}
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @see #restart(String, IServer.IOperationListener)
	 */
	public void restart(String launchMode, IProgressMonitor monitor);

	/**
	 * Asynchronously restarts this server. This operation does
	 * nothing if this server cannot be stopped ({@link #canRestart(String)}
	 * returns <code>false</code>.
	 * This method cannot be used to start the server from a stopped state.
	 * <p>
	 * The operation listener can be used to add a listener for notification
	 * of this specific server restart. The listener will be called with a
	 * single successful status (severity OK) when the server has
	 * finished restarting, or a single failure (severity ERROR) if
	 * there was an error restarting the server.
	 * </p>
	 *
	 * @param launchMode a mode in which a server can be launched,
	 *    one of the mode constants defined by
	 *    {@link org.eclipse.debug.core.ILaunchManager}
	 * @param listener an operation listener to receive notification when this
	 *    operation is done, or <code>null</code> if notification is not
	 *    required
	 */
	public void restart(String launchMode, IOperationListener listener);

	/**
	 * Returns whether this server is in a state that it can
	 * be stopped.
	 * Servers can be stopped if they are not already stopped and if
	 * they belong to a state-set that can be stopped.
	 *
	 * @return a status object with code <code>IStatus.OK</code> if the server can
	 *   be stopped, otherwise a status object indicating why it can't
	 */
	public IStatus canStop();

	/**
	 * Asynchronously stops this server. This operation does
	 * nothing if this server cannot be stopped ({@link #canStop()}
	 * returns <code>false</code>.
	 * <p>
	 * If force is <code>false</code>, it will attempt to stop the server
	 * normally/gracefully. If force is <code>true</code>, then the server
	 * process will be terminated any way that it can.
	 * </p>
	 * <p>
	 * If the caller wants to listen for success or failure of the
	 * server stopping, it can add a server listener or use the
	 * version of this method that takes an operation listener as a
	 * parameter.
	 * </p>
	 * 
	 * @param force <code>true</code> to kill the server, or <code>false</code>
	 *    to stop normally
	 * @see #start(String, IServer.IOperationListener)
	 */
	public void stop(boolean force);

	/**
	 * Asynchronously stops this server. This operation does
	 * nothing if this server cannot be stopped ({@link #canStop()}
	 * returns <code>false</code>.
	 * <p>
	 * If force is <code>false</code>, it will attempt to stop the server
	 * normally/gracefully. If force is <code>true</code>, then the server
	 * process will be terminated any way that it can.
	 * </p>
	 * <p>
	 * The operation listener can be used to add a listener for notification
	 * of this specific server stop. The listener will be called with a
	 * single successful status (severity OK) when the server has
	 * finished stopping, or a single failure (severity ERROR) if
	 * there was an error stopping the server.
	 * </p>
	 * 
	 * @param force <code>true</code> to kill the server, or <code>false</code>
	 *    to stop normally
	 * @param listener an operation listener to receive notification when this
	 *    operation is done, or <code>null</code> if notification is not
	 *    required
	 */
	public void stop(boolean force, IOperationListener listener);

	/**
	 * Returns the current state of the given module on this server.
	 * Returns <code>STATE_UNKNOWN</code> if the module
	 * is not among the ones associated with this server.
	 * 
	 * @param module the module
	 * @return one of the state (<code>STATE_XXX</code>) constants declared
	 *    on {@link IServer}
	 */
	public int getModuleState(IModule[] module);

	/**
	 * Returns the module's sync state.
	 * 
	 * @param module the module
	 * @return one of the PUBLISH_STATE_XXX state flags
	 */
	public int getModulePublishState(IModule[] module);

	/**
	 * Check if the given module is in sync on the server. It should
	 * return true if the module should be restarted (is out of
	 * sync) or false if the module does not need to be restarted.
	 *
	 * @param module the module
	 * @return boolean
	 */
	public boolean getModuleRestartState(IModule[] module);

	/**
	 * Returns whether the given module can be restarted.
	 * <p>
	 * This method has a progress monitor because it may involve plugin
	 * and class loading. No communication to the server will occur.
	 * </p>
	 * 
	 * @param module the module
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a status object with code <code>IStatus.OK</code> if the module can
	 *    be restarted, otherwise a status object indicating why it can't
	 */
	public IStatus canControlModule(IModule[] module, IProgressMonitor monitor);

	/**
	 * Asynchronously starts this server in the given launch mode.
	 * <p>
	 * If canStart(launchMode) is false, this method will throw an
	 * exception.
	 * </p>
	 * <p>
	 * The operation listener can be used to add a listener for notification
	 * of this specific module start. The listener will be called with a
	 * single successful status (severity OK) when the module has
	 * finished starting, or a single failure (severity ERROR) if
	 * there was an error starting the module.
	 * </p>
	 *
	 *@param module the module to be started
	 * @param listener an operation listener to receive notification when this
	 *    operation is done, or <code>null</code> if notification is not
	 *    required
	 */
	public void startModule(IModule[] module, IOperationListener listener);

	/**
	 * Asynchronously stops the given module. This operation does
	 * nothing if this module cannot be stopped.
	 * <p>
	 * The operation listener can be used to add a listener for notification
	 * of this specific module stop. The listener will be called with a
	 * single successful status (severity OK) when the module has
	 * finished stopping, or a single failure (severity ERROR) if
	 * there was an error stopping the module.
	 * </p>
	 * 
	 * @param module the module to be stopped
	 * @param listener an operation listener to receive notification when this
	 *    operation is done, or <code>null</code> if notification is not
	 *    required
	 */
	public void stopModule(IModule[] module, IOperationListener listener);

	/**
	 * Asynchronously restarts the given module on the server.
	 * <p>
	 * The implementation should update the module sync state and fire
	 * an event for the module. If the module does not exist on the server,
	 * an exception will be thrown.
	 * </p>
	 * <p>
	 * [issue: If the module was just published to the server
	 * and had never been started, would is be ok to "start"
	 * the module using this method?]
	 * </p>
	 * 
	 * @param module the module to be started
	 * @param listener an operation listener to receive notification when this
	 *    operation is done, or <code>null</code> if notification is not
	 *    required
	 */
	public void restartModule(IModule[] module, IOperationListener listener);

	/**
	 * Return the launch configuration for this server. If one does not exist, it
	 * will be created if "create" is true, and otherwise will return <code>null</code>.
	 * Will return <code>null</code> if this server type is invalid or has no associated
	 * launch configuration type (i.e. this server type cannot be started).
	 * 
	 * @param create <code>true</code> if a new launch configuration should be
	 *    created if there are none already
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return the launch configuration, or <code>null</code> if there was no
	 *    existing launch configuration and <code>create</code> was false
	 * @throws CoreException
	 * @since 2.0
	 */
	public ILaunchConfiguration getLaunchConfiguration(boolean create, IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns the launch that was used to start the server, if available. If the server
	 * is not running, will return <code>null</code>. 
	 * 
	 * @return the launch used to start the currently running server, or <code>null</code>
	 *    if the launch is unavailable or could not be found
	 * @since 3.0
	 */
	public ILaunch getLaunch();

	/**
	 * Returns the start timeout in seconds.
	 * 
	 * @return the start timeout in seconds
	 * @since 3.0
	 */
	public int getStartTimeout();

	/**
	 * Returns the stop timeout in seconds.
	 * 
	 * @return the stop timeout in seconds
	 * @since 3.0
	 */
	public int getStopTimeout();

	/**
	 * Starts this server in the given launch mode and waits until the server
	 * has finished starting.
	 * <p>
	 * This convenience method uses {@link #start(String, IProgressMonitor)}
	 * to start the server, and an internal thread and listener to detect
	 * when the server has finished starting.
	 * </p>
	 *
	 * @param launchMode a mode in which a server can be launched,
	 *    one of the mode constants defined by
	 *    {@link org.eclipse.debug.core.ILaunchManager}
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @deprecated use {@link #start(String, IServer.IOperationListener)}
	 *    instead
	 * @exception CoreException if an error occurs while trying to start the server
	 */
	public void synchronousStart(String launchMode, IProgressMonitor monitor) throws CoreException;

	/**
	 * Stops this server and waits until the server has completely stopped.
	 * <p>
	 * This convenience method uses {@link #stop(boolean)}
	 * to stop the server, and an internal thread and listener to detect
	 * when the server has complied.
	 * </p>
	 * 
	 * @param force <code>true</code> to kill the server, or <code>false</code>
	 *    to stop normally
	 * @deprecated use {@link #stop(boolean, IOperationListener)}
	 *    instead
	 */
	public void synchronousStop(boolean force);

	/**
	 * Synchronously restarts this server. This operation does
	 * nothing if this server cannot be stopped ({@link #canRestart(String)}
	 * returns <code>false</code>.
	 * <p>
	 *
	 * @param launchMode a mode in which a server can be launched,
	 *    one of the mode constants defined by
	 *    {@link org.eclipse.debug.core.ILaunchManager}
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there was an error
	 * @deprecated use {@link #restart(String, IServer.IOperationListener)} 
	 *    instead
	 */
	public void synchronousRestart(String launchMode, IProgressMonitor monitor) throws CoreException;
}