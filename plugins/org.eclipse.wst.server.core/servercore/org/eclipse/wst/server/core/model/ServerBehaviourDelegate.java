/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.*;
/**
 * A server delegate provides the implementation for various 
 * generic and server-type-specific operations for a specific type of server.
 * A server delegate is specified by the
 * <code>class</code> attribute of a <code>serverTypes</code> extension.
 * <p>
 * When the server instance needs to be given a delegate, the delegate class
 * specified for the server type is instantiated with a 0-argument constructor
 * and primed with <code>delegate.initialize(((IServerState)server)</code>, 
 * which it is expected to hang on to. Later, when
 * <code>delegate.dispose()</code> is called as the server instance is
 * being discarded, the delegate is expected to let go of the server instance.
 * </p>
 * <p>
 * Server delegates may keep state in instance fields, but that state is
 * transient and will not be persisted across workbench sessions.
 * </p>
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>serverTypes</code> extension point.
 * </p>
 * 
 * @noimplement
 * @see IServer
 * @see IServerWorkingCopy
 * @since 1.0
 */
public abstract class ServerBehaviourDelegate {
	private Server server;

	/**
	 * Publish kind constant (value 0) for no change.
	 * 
	 * @see #publishModule(int, int, IModule[], IProgressMonitor)
	 */
	public static final int NO_CHANGE = 0;

	/**
	 * Publish kind constant (value 1) for added resources.
	 * 
	 * @see #publishModule(int, int, IModule[], IProgressMonitor)
	 */
	public static final int ADDED = 1;

	/**
	 * Publish kind constant (value 2) for changed resources.
	 * 
	 * @see #publishModule(int, int, IModule[], IProgressMonitor)
	 */
	public static final int CHANGED = 2;

	/**
	 * Publish kind constant (value 3) for removed resources.
	 * 
	 * @see #publishModule(int, int, IModule[], IProgressMonitor)
	 */
	public static final int REMOVED = 3;

	/**
	 * Delegates must have a public 0-arg constructor.
	 */
	public ServerBehaviourDelegate() {
		// do nothing
	}

	/**
	 * Initializes this server delegate with its life-long server instance.
	 * <p>
	 * This method is called by the server core framework.
	 * Clients should never call this method.
	 * </p>
	 * 
	 * @param newServer the server instance
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 */
	final void initialize(Server newServer, IProgressMonitor monitor) {
		server = newServer;
		initialize(monitor);
	}

	/**
	 * Initializes this server delegate. This method gives delegates a chance
	 * to do their own initialization.
	 * <p>
	 * If the server state is initially unknown, this method should attempt
	 * to connect to the server and update the state. On servers where the
	 * state may change, this is also an excellent place to create a background
	 * thread that will constantly ping the server (or have a listener) to
	 * update the server state as changes occur.
	 * </p>
	 * <p>
	 * This method is called by the server core framework.
	 * Clients should never call this method.
	 * </p>
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 */
	protected void initialize(IProgressMonitor monitor) {
		// do nothing
	}

	/**
	 * Returns the server that this server delegate corresponds to.
	 * 
	 * @return the server
	 */
	public final IServer getServer() {
		return server;
	}

	/**
	 * Sets the current state of this server.
	 *
	 * @param state the current state of the server, one of the state
	 *    constants defined by {@link IServer}
	 * @see IServer#getServerState()
	 */
	protected final void setServerState(int state) {
		server.setServerState(state);
	}

	/**
	 * Sets the ILaunchManager mode that the server is running in. The server
	 * implementation will automatically return <code>null</code> to clients
	 * when the server is stopped, so you only need to update the mode when
	 * it changes.
	 * 
	 * @param mode the mode in which a server is running, one of the mode constants
	 *    defined by {@link ILaunchManager}
	 */
	protected final void setMode(String mode) {
		server.setMode(mode);
	}

	/**
	 * Sets the Launch on the server object. 
	 *  
	 * @param launch
	 */
	public final void setLaunch(ILaunch launch) {
		server.setLaunch(launch);
	}
	
	/**
	 * Sets the server restart state.
	 *
	 * @param state <code>true</code> if the server needs to be restarted,
	 *    and <code>false</code> otherwise
	 */
	protected final void setServerRestartState(boolean state) {
		server.setServerRestartState(state);
	}

	/**
	 * Sets the server publish state.
	 *
	 * @param state the current publish state of the server, one of the
	 *    publish constants defined by {@link IServer}
	 */
	protected final void setServerPublishState(int state) {
		server.setServerPublishState(state);
	}

	/**
	 * Hook to fire an event when a module state changes.
	 * 
	 * @param module the module
	 * @param state the current state of the module, one of the state
	 *    constants defined by {@link IServer}
	 */
	protected final void setModuleState(IModule[] module, int state) {
		server.setModuleState(module, state);
	}

	/**
	 * Sets the module publish state.
	 *
	 * @param module the module
	 * @param state the current publish state of the module, one of the
	 *    publish constants defined by {@link IServer}
	 */
	protected final void setModulePublishState(IModule[] module, int state) {
		server.setModulePublishState(module, state);
	}

	/**
	 * Sets the module restart state.
	 *
	 * @param module the module
	 * @param state <code>true</code> if the module needs to be restarted,
	 *    and <code>false</code> otherwise
	 */
	protected final void setModuleRestartState(IModule[] module, boolean state) {
		server.setModuleRestartState(module, state);
	}

	/**
	 * Sets the server's external modules.
	 *
	 * @param modules the root external module
	 */
	protected final void setExternalModules(IModule[] modules) {
		server.setExternalModules(modules);
	}

	/**
	 * Creates an external module instance with the given static information. An external module is a unit of "content"
	 * that is already published to the server, and that doesn't exist in the workspace
	 *  
	 * @param id the module id
	 * @param name the module name
	 * @param type the module type id
	 * @param version the module version id
	 * @param moduleDelegate the ModuleDelegate which will act as a helper of the module 
	 * @return a module instance
	 */
	protected final IModule createExternalModule(String id, String name, String type, String version, ModuleDelegate moduleDelegate) {
		IModule module = new ExternalModule(id, name, type, version, moduleDelegate);
		setModulePublishState(new IModule [] {module}, IServer.PUBLISH_STATE_NONE);
		return module;
	}

	/**
	 * Disposes of this server delegate.
	 * <p>
	 * This method is called by the server core framework.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * Implementations are expected to let go of the delegate's reference
	 * to the server, deregister listeners, etc.
	 * </p>
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * Methods called to notify that publishing is about to begin.
	 * This allows the server to open a connection to the server
	 * or get any global information ready.
	 * <p>
	 * This method is called by the server core framework,
	 * in response to a call to <code>IServer.publish()</code>.
	 * Clients should never call this method.
	 * </p>
	 *
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is a problem starting the publish
	 */
	protected void publishStart(IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Publish the server.
	 * <p>
	 * This method is called by the server core framework,
	 * in response to a call to <code>IServer.publish()</code>.
	 * Clients should never call this method.
	 * </p>
	 * 
	 * @param kind one of the IServer.PUBLISH_XX constants. Valid values are
	 *    <ul>
	 *    <li><code>PUBLISH_FULL</code>- indicates a full publish.</li>
	 *    <li><code>PUBLISH_INCREMENTAL</code>- indicates a incremental publish.
	 *    <li><code>PUBLISH_AUTO</code>- indicates an automatic incremental publish.</li>
	 *    <li><code>PUBLISH_CLEAN</code>- indicates a clean request. Clean throws
	 *      out all state and cleans up the module on the server before doing a
	 *      full publish.
	 *    </ul>
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is a problem publishing the server
	 */
	protected void publishServer(int kind, IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Publish a single module to the server.
	 * <p>
	 * This method is called by the server core framework,
	 * in response to a call to <code>IServer.publish()</code>.
	 * Clients should never call this method directly.
	 * </p>
	 * <p>
	 * If the deltaKind is IServer.REMOVED, the module may have been completely
	 * deleted and does not exist anymore. In this case, a dummy module (with the
	 * correct id) will be passed to this method.
	 * </p>
	 * <p>
	 * It is recommended that clients implementing this method be responsible for
	 * setting the module state.
	 * </p>
	 * 
	 * @param kind one of the IServer.PUBLISH_XX constants. Valid values are:
	 *    <ul>
	 *    <li><code>PUBLISH_FULL</code>- indicates a full publish.</li>
	 *    <li><code>PUBLISH_INCREMENTAL</code>- indicates a incremental publish.
	 *    <li><code>PUBLISH_AUTO</code>- indicates an automatic incremental publish.</li>
	 *    <li><code>PUBLISH_CLEAN</code>- indicates a clean request. Clean throws
	 *      out all state and cleans up the module on the server before doing a
	 *      full publish.
	 *    </ul>
	 * @param module the module to publish
	 * @param deltaKind one of the IServer publish change constants. Valid values are:
	 *    <ul>
	 *    <li><code>ADDED</code>- indicates the module has just been added to the server
	 *      and this is the first publish.
	 *    <li><code>NO_CHANGE</code>- indicates that nothing has changed in the module
	 *      since the last publish.</li>
	 *    <li><code>CHANGED</code>- indicates that the module has been changed since
	 *      the last publish. Call <code>getPublishedResourceDelta()</code> for
	 *      details of the change.
	 *    <li><code>REMOVED</code>- indicates the module has been removed and should be
	 *      removed/cleaned up from the server.
	 *    </ul>
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is a problem publishing the module
	 */
	protected void publishModule(int kind, int deltaKind, IModule[] module, IProgressMonitor monitor) throws CoreException {
		// by default, assume the module has published successfully.
		// this will update the publish state and delta correctly
		setModulePublishState(module, IServer.PUBLISH_STATE_NONE);
	}

	/**
	 * Methods called to notify that publishing has finished.
	 * The server can close any open connections to the server
	 * and do any cleanup operations.
	 * <p>
	 * This method is called by the server core framework,
	 * in response to a call to <code>IServer.publish()</code>.
	 * Clients should never call this method.
	 * </p>
	 *
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is a problem stopping the publish
	 */
	protected void publishFinish(IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Configure the given launch configuration to start this server. This method is called whenever
	 * the server is started to ensure that the launch configuration is accurate and up to date.
	 * This method should not blindly update the launch configuration in cases where the user has
	 * access to change the launch configuration by hand.
	 * 
	 * @param workingCopy a launch configuration working copy
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is an error setting up the configuration
	 */
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Restart this server using a more optimized behavior than a full stop
	 * and start. The server should use the server listener to notify progress.
	 * 
	 * This method is used to find out if there is an optimized path to restart
	 * the server that's better than a full stop and start (launch). If this
	 * method throws a CoreException with a status code of -1, it is a sign
	 * that either there is no optimized path, or that the path has failed,
	 * and in either case the framework will use the regular stop/start
	 * behavior.
	 * 
	 * @param launchMode the mode to restart in, one of the mode constants
	 *    defined by {@link ILaunchManager}
	 * @throws CoreException if there was a problem restarting
	 */
	public void restart(String launchMode) throws CoreException {
		throw new CoreException(new Status(IStatus.WARNING, ServerPlugin.PLUGIN_ID, -1, "Restart not supported", null));
	}

	/**
	 * Returns whether the given module can be restarted.
	 * <p>
	 * This call should complete reasonably fast and not require communication
	 * with the (potentially remote) server. If communication is required it
	 * should be done asynchronously and this method should either fail until
	 * that is complete or succeed and handle failure in the xxModule methods.
	 * </p>
	 * 
	 * @param module the module
	 * @return <code>true</code> if the given module can be
	 *    restarted, and <code>false</code> otherwise
	 *    
	 * @deprecated instead use canRestartModule, canPublishModule   
	 */
	public boolean canControlModule(IModule[] module) {
		return false;
	}
	
	/**
	 * Returns whether the given module can be restarted.
	 * <p>
	 * This call should complete reasonably fast and not require communication
	 * with the (potentially remote) server. If communication is required it
	 * should be done asynchronously and this method should either fail until
	 * that is complete or succeed and handle failure in the xxModule methods.
	 * </p>
	 * 
	 * @param module the module
	 * @return <code>true</code> if the given module can be
	 *    restarted, and <code>false</code> otherwise
	 *    
	 */
	public boolean canRestartModule(IModule[] module){
		return canControlModule(module);
	}
	
	/**
	 * Returns whether the given module can be published.
	 * <p>
	 * This call should complete reasonably fast and not require communication
	 * with the (potentially remote) server. If communication is required it
	 * should be done asynchronously and this method should either fail until
	 * that is complete or succeed and handle failure in the xxModule methods.
	 * </p>
	 * 
	 * @param module the module
	 * @return <code>true</code> if the given module can be
	 *    published, and <code>false</code> otherwise
	 *    
	 */
	public boolean canPublishModule(IModule[] module){
		return canControlModule(module);
	}

	/**
	 * Returns whether this server is in a state that it can
	 * be started in the given mode.
	 * <p>
	 * This call should complete reasonably fast and not require communication
	 * with the (potentially remote) server. If communication is required it
	 * should be done asynchronously and this method should either fail until
	 * that is complete or succeed and handle failure during start.
	 * </p><p>
	 * This method is called by the server core framework,
	 * in response to a call to <code>IServer.canStart()</code>.
	 * The framework has already filtered out obviously invalid situations,
	 * such as starting a server that is already running.
	 * Clients should never call this method directly.
	 * </p>
	 * 
	 * @param launchMode a mode in which a server can be launched,
	 *    one of the mode constants defined by
	 *    {@link org.eclipse.debug.core.ILaunchManager}
	 * @return a status object with code <code>IStatus.OK</code> if the server can
	 *    be started, otherwise a status object indicating why it can't
    * @since 1.1
	 */
	public IStatus canStart(String launchMode) {
		return Status.OK_STATUS;
	}

	/**
	 * Returns whether this server is in a state that it can
	 * be restarted in the given mode. Note that only servers
	 * that are currently running can be restarted.
	 * <p>
	 * This call should complete reasonably fast and not require communication
	 * with the (potentially remote) server. If communication is required it
	 * should be done asynchronously and this method should either fail until
	 * that is complete or succeed and handle failure during restart.
	 * </p><p>
	 * This method is called by the server core framework,
	 * in response to a call to <code>IServer.canRestart()</code>.
	 * The framework has already filtered out obviously invalid situations,
	 * such as restarting a stopped server.
	 * Clients should never call this method directly.
	 * </p>
	 * 
	 * @param mode a mode in which a server can be launched,
	 *    one of the mode constants defined by
	 *    {@link org.eclipse.debug.core.ILaunchManager}
	 * @return a status object with code <code>IStatus.OK</code> if the server can
	 *    be restarted, otherwise a status object indicating why it can't
    * @since 1.1
	 */
	public IStatus canRestart(String mode) {
		return Status.OK_STATUS;
	}

	/**
	 * Returns whether this server is in a state that it can
	 * be stopped.
	 * Servers can be stopped if they are not already stopped and if
	 * they belong to a state-set that can be stopped.
	 * <p>
	 * This call should complete reasonably fast and not require communication
	 * with the (potentially remote) server. If communication is required it
	 * should be done asynchronously and this method should either fail until
	 * that is complete or succeed and handle failure during stop.
	 * </p><p>
	 * This method is called by the server core framework,
	 * in response to a call to <code>IServer.canStop()</code>.
	 * The framework has already filtered out obviously invalid situations,
	 * such as stopping a server that is already stopped.
	 * Clients should never call this method directly.
	 * </p>
	 * 
	 * @return a status object with code <code>IStatus.OK</code> if the server can
	 *   be stopped, otherwise a status object indicating why it can't
    * @since 1.1
	 */
	public IStatus canStop() {
		return Status.OK_STATUS;
	}

	/**
	 * Returns whether this server is in a state that it can
	 * be published to.
	 * <p>
	 * This call should complete reasonably fast and not require communication
	 * with the (potentially remote) server. If communication is required it
	 * should be done asynchronously and this method should either fail until
	 * that is complete or succeed and handle failure during publish.
	 * </p><p>
	 * This method is called by the server core framework,
	 * in response to a call to <code>IServer.canPublish()</code>.
	 * The framework has already filtered out obviously invalid situations,
	 * such as publishing to a server in the wrong mode.
	 * Clients should never call this method directly.
	 * </p>
	 * 
	 * @return a status object with code <code>IStatus.OK</code> if the server can
	 *   be published to, otherwise a status object indicating what is wrong
    * @since 1.1
	 */
	public IStatus canPublish() {
		return Status.OK_STATUS;
	}

	/**
	 * Starts the given module on the server. See the specification of 
	 * {@link IServer#startModule(IModule[], IServer.IOperationListener)}
	 * for further details. 
	 * <p>
	 * The implementation should update the module sync state and fire
	 * an event for the module.
	 * </p>
	 * <p>
	 * This method will throw an exception if the module does not exist on
	 * the server.
	 * </p>
	 * <p>
	 * [issue: Since this method is asynchronous, is there
	 * any need for the progress monitor?]
	 * </p>
	 * 
	 * @param module the module to be started
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception CoreException if an error occurs while trying to restart the module
	 */
	public void startModule(IModule[] module, IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Stops the given module on the server. See the specification of 
	 * {@link IServer#stopModule(IModule[], IServer.IOperationListener)}
	 * for further details. 
	 * <p>
	 * The implementation should update the module sync state and fire
	 * an event for the module.
	 * </p>
	 * <p>
	 * This method will throw an exception if the module does not exist on
	 * the server.
	 * </p>
	 * <p>
	 * [issue: Since this method is asynchronous, is there
	 * any need for the progress monitor?]
	 * </p>
	 * 
	 * @param module the module to be stopped
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception CoreException if an error occurs while trying to restart the module
	 */
	public void stopModule(IModule[] module, IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Restarts the given module on the server. See the specification of 
	 * {@link IServer#restartModule(IModule[], IServer.IOperationListener)}
	 * for further details. 
	 * <p>
	 * The implementation should update the module sync state and fire
	 * an event for the module.
	 * </p>
	 * <p>
	 * This method will throw an exception if the module does not exist on
	 * the server.
	 * </p>
	 * <p>
	 * [issue: Since this method is asynchronous, is there
	 * any need for the progress monitor?]
	 * </p>
	 * 
	 * @param module the module to be stopped
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception CoreException if an error occurs while trying to restart the module
	 */
	public void restartModule(IModule[] module, IProgressMonitor monitor) throws CoreException {
		stopModule(module, monitor);
		startModule(module, monitor);
	}

	/**
	 * Shuts down and stops this server. The server should return from this method
	 * quickly and use the server listener to notify shutdown progress.
	 * <p> 
	 * If force is <code>false</code>, it will attempt to stop the server
	 * normally/gracefully. If force is <code>true</code>, then the server
	 * process will be terminated any way that it can.
	 * </p>
	 * <p>
	 * [issue: There is no way to communicate failure to the
	 * client. Given that this operation can go awry, there probably
	 * should be a mechanism that allows failing async operations
	 * to be diagnosed.]
	 * </p>
	 * @param force <code>true</code> to kill the server, or <code>false</code>
	 *    to stop normally
	 */
	public abstract void stop(boolean force);

	/**
	 * Returns the current module resources.
	 * 
	 * @param module the module
	 * @return an array containing the module's resources
	 */
	protected IModuleResource[] getResources(IModule[] module) {
		return server.getResources(module);
	}

	/**
	 * Returns the module resources that have been published to the server.
	 * 
	 * <p>
	 * If the module has just been added to the server, an empty list will
	 * be returned. If the module has never existed on the server, a CoreException
	 * will be thrown.
	 * </p>
	 * 
	 * @param module the module
	 * @return an array containing the published module resource
	 */
	protected IModuleResource[] getPublishedResources(IModule[] module) {
		return server.getPublishedResources(module);
	}

	/**
	 * Returns the delta of the current module resources that have been
	 * published compared to the current state of the module.
	 *
	 * @param module the module
	 * @return an array containing the publish resource delta
	 */
	protected IModuleResourceDelta[] getPublishedResourceDelta(IModule[] module) {
		return server.getPublishedResourceDelta(module);
	}

	/**
	 * Returns a temporary directory that the requester can use
	 * throughout it's lifecycle. This is primarily to be used by
	 * servers for working directories, server specific
	 * files, etc.
	 * <p>
	 * This method directory will return the same directory on
	 * each use of the workbench. If the directory is not requested
	 * over a period of time, the directory may be deleted and a
	 * new one will be assigned on the next request. For this
	 * reason, a server may want to request the temp directory on
	 * startup if it wants to store files there. In any case, the
	 * server should have a backup plan to refill the directory
	 * in case it has been deleted since last use.</p>
	 *
	 * @return a temporary directory
	 */
	protected IPath getTempDirectory() {
		return server.getTempDirectory();
	}

	/**
	 * Returns a temporary directory that the requester can use
	 * throughout it's lifecycle. This is primarily to be used by
	 * servers for working directories, server specific
	 * files, etc.
	 * <p>
	 * As long as the same key is used to call this method on
	 * each use of the workbench, this method directory will return
	 * the same directory. If recycling is enabled and the directory
	 * is not requested over a period of time, the directory may be
	 * deleted and a new one will be assigned on the next request.
	 * If this behavior is not desired, recycling should be disabled.</p>
	 *
	 * @param recycle true if directory may be deleted if not used
	 * over a period of time
	 * @return a temporary directory
	 */
	protected IPath getTempDirectory(boolean recycle) {
		return server.getTempDirectory(recycle);
	}

	/**
	 * Set a global status on the server.
	 *  
	 * @param status the status
	 */
	protected final void setServerStatus(IStatus status) {
		server.setServerStatus(status);
	}

	/**
	 * Set a status on a specific module.
	 * 
	 * @param module the module
	 * @param status the status
	 */
	protected final void setModuleStatus(IModule[] module, IStatus status) {
		server.setModuleStatus(module, status);
	}

	/**
	 * Publish to the server.
	 * 
	 * @param kind the publish kind
	 * @param modules
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @param info
	 * @throws CoreException
	 */
	public void publish(int kind, List<IModule[]> modules, IProgressMonitor monitor, IAdaptable info) throws CoreException {
		info2 = info;
		IStatus status = publish(kind, monitor);
		if (status != null && status.getSeverity() != IStatus.OK && status.getSeverity() != IStatus.CANCEL)
			throw new CoreException(status);
	}

	private IAdaptable info2;

	/*public void publish2(int kind, List<IModule[]> modules, IProgressMonitor monitor, IAdaptable info) throws CoreException {
		Trace.trace(Trace.FINEST, "-->-- Publishing to server: " + toString() + " -->--");
		
		if (getServer().getServerType().hasRuntime() && getServer().getRuntime() == null)
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorPublishNoRuntime, null));
		
		final List<IModule[]> moduleList = getAllModules();
		addRemovedModules(moduleList, null);
		
		PublishOperation[] tasks = getTasks(kind, moduleList, null);
		int size = 2000 + 3500 * moduleList.size() + 500 * tasks.length;
		
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(NLS.bind(Messages.publishing, getServer().getName()), size);
		
		MultiStatus tempMulti = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, "", null);
		
		if (monitor.isCanceled())
			return;
		
		try {
			Trace.trace(Trace.FINEST, "Starting publish");
			publishStart(ProgressUtil.getSubMonitorFor(monitor, 1000));
			
			if (monitor.isCanceled())
				return;
			
			// execute publishers
			executePublishers(kind, modules, monitor, info);
			
			if (monitor.isCanceled())
				return;
			
			// publish the server
			publishServer(kind, ProgressUtil.getSubMonitorFor(monitor, 1000));
			
			if (monitor.isCanceled())
				return;
			
			// publish modules
			IspublishModules(kind, moduleList, monitor);
			
			if (monitor.isCanceled())
				return;
			
			monitor.done();
		} catch (CoreException ce) {
			Trace.trace(Trace.INFO, "CoreException publishing to " + toString(), ce);
			throw ce;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error publishing  to " + toString(), e);
			tempMulti.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorPublishing, e));
		} finally {
			// end the publishing
			try {
				publishFinish(ProgressUtil.getSubMonitorFor(monitor, 500));
			} catch (CoreException ce) {
				Trace.trace(Trace.INFO, "CoreException publishing to " + toString(), ce);
				tempMulti.add(ce.getStatus());
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error stopping publish to " + toString(), e);
				tempMulti.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorPublishing, e));
			}
		}
		
		Trace.trace(Trace.FINEST, "--<-- Done publishing --<--");
		
		if (tempMulti.getChildren().length == 1)
			throw new CoreException(tempMulti.getChildren()[0]);
		
		MultiStatus multi = null;
		if (tempMulti.getSeverity() == IStatus.OK)
			return;
		else if (tempMulti.getSeverity() == IStatus.INFO)
			multi = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, Messages.publishingStatusInfo, null);
		else if (tempMulti.getSeverity() == IStatus.WARNING)
			multi = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, Messages.publishingStatusWarning, null);
		else if (tempMulti.getSeverity() == IStatus.ERROR)
			multi = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, Messages.publishingStatusError, null);
		
		if (multi != null)
			multi.addAll(tempMulti);
	}*/

	private List<Integer> computeDelta(final List<IModule[]> moduleList) {

		final List<Integer> deltaKindList = new ArrayList<Integer>();
		final Iterator<IModule[]> iterator = moduleList.iterator();
		while (iterator.hasNext()) {
			IModule[] module = iterator.next();
			if (hasBeenPublished(module)) {
				IModule m = module[module.length - 1];
				if ((m.getProject() != null && !m.getProject().isAccessible())
						|| getPublishedResourceDelta(module).length == 0) {
					deltaKindList.add(new Integer(ServerBehaviourDelegate.NO_CHANGE));
				}
				else {
					deltaKindList.add(new Integer(ServerBehaviourDelegate.CHANGED));
				}
			}
			else {
				deltaKindList.add(new Integer(ServerBehaviourDelegate.ADDED));
			}
		}
		this.addRemovedModules(moduleList, null);
		while (deltaKindList.size() < moduleList.size()) {
			deltaKindList.add(new Integer(ServerBehaviourDelegate.REMOVED));
		}
		return deltaKindList;
	}
	
	/**
	 * Publish to the server.
	 * 
	 * @param kind the publish kind
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return the publish status
	 */
	public IStatus publish(int kind, IProgressMonitor monitor) {
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "-->-- Publishing to server: " + getServer().toString() + " -->--");
		}
		
		if (getServer().getServerType().hasRuntime() && getServer().getRuntime() == null)
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorPublishNoRuntime, null);
		
		final List<IModule[]> moduleList = getAllModules();
		List<Integer> deltaKindList = this.computeDelta(moduleList);
		
		PublishOperation[] tasks = getTasks(kind, moduleList, deltaKindList);
		int size = 2000 + 3500 * moduleList.size() + 500 * tasks.length;
		
		monitor = ProgressUtil.getMonitorFor(monitor);
		String mainTaskMsg = NLS.bind(Messages.publishing, getServer().getName());
		monitor.beginTask(mainTaskMsg, size);
		
		MultiStatus tempMulti = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, "", null);
		
		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;
		
		try {
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Starting publish");
			}
			publishStart(ProgressUtil.getSubMonitorFor(monitor, 1000));
			
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			// execute tasks
			MultiStatus taskStatus = performTasks(tasks, monitor);
			monitor.setTaskName(mainTaskMsg);
			if (taskStatus != null && !taskStatus.isOK())
				tempMulti.addAll(taskStatus);
			
			// execute publishers
			taskStatus = executePublishers(kind, moduleList, deltaKindList, monitor, info2);
			monitor.setTaskName(mainTaskMsg);
			if (taskStatus != null && !taskStatus.isOK())
				tempMulti.addAll(taskStatus);
			
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			// publish the server
			publishServer(kind, ProgressUtil.getSubMonitorFor(monitor, 1000));
			monitor.setTaskName(mainTaskMsg);
			
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			// publish modules
			publishModules(kind, moduleList, deltaKindList, tempMulti, monitor);
			
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			monitor.done();
		} catch (CoreException ce) {
			if (Trace.INFO) {
				Trace.trace(Trace.STRING_INFO, "CoreException publishing to " + toString(), ce);
			}
			return ce.getStatus();
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error publishing  to " + toString(), e);
			}
			tempMulti.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorPublishing, e));
		} finally {
			// end the publishing
			try {
				publishFinish(ProgressUtil.getSubMonitorFor(monitor, 500));
			} catch (CoreException ce) {
				if (Trace.INFO) {
					Trace.trace(Trace.STRING_INFO, "CoreException publishing to " + toString(), ce);
				}
				tempMulti.add(ce.getStatus());
			} catch (Exception e) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Error stopping publish to " + toString(), e);
				}
				tempMulti.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorPublishing, e));
			}
		}
		
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "--<-- Done publishing --<--");
		}
		
		if (tempMulti.getChildren().length == 1)
			return tempMulti.getChildren()[0];
		
		MultiStatus multi = null;
		if (tempMulti.getSeverity() == IStatus.OK)
			return Status.OK_STATUS;
		else if (tempMulti.getSeverity() == IStatus.INFO)
			multi = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, Messages.publishingStatusInfo, null);
		else if (tempMulti.getSeverity() == IStatus.WARNING)
			multi = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, Messages.publishingStatusWarning, null);
		else if (tempMulti.getSeverity() == IStatus.ERROR)
			multi = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, Messages.publishingStatusError, null);
		
		if (multi != null)
			multi.addAll(tempMulti);
		
		return multi;
	}

	/*private void printModule(IModuleResource[] res, String ind) {
		int size = res.length;
		for (int i = 0; i < size; i++) {
			if (res[i] instanceof IModuleFolder) {
				IModuleFolder f = (IModuleFolder) res[i];
				printModule(f.members(), ind + "  ");
			} else {
				Trace.trace(Trace.INFO, ind + res[i].getName());
			}
		}
	}*/

	/**
	 * Publish a single module.
	 * 
	 * @param kind a publish kind
	 * @param module a module
	 * @param deltaKind the delta kind
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return the status
	 */
	protected IStatus publishModule(int kind, IModule[] module, int deltaKind, IProgressMonitor monitor) {
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "-->-- Publishing module");
		}
		
		int size = module.length;
		IModule m = module[size - 1];
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Module: " + m);
		}
		monitor.beginTask(NLS.bind(Messages.publishingModule, m.getName()), 1000);
		
		try {
			publishModule(kind, deltaKind, module, monitor);
			return Status.OK_STATUS;
		} catch (CoreException ce) {
			return ce.getStatus();
		} catch (Exception e) {
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorPublishing, e);
		} finally {
			monitor.done();
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "--<-- Done publishing module");
			}
		}
	}

	/**
	 * Returns <code>true</code> if the given module has been published, and
	 *    <code>false</code> otherwise.
	 * 
	 * @param module a module
	 * @return <code>true</code> if the given module has been published, and
	 *    <code>false</code> otherwise
	 */
	protected boolean hasBeenPublished(IModule[] module) {
		return server.getServerPublishInfo().hasModulePublishInfo(module);
	}

	/**
	 * Adds removed modules.
	 * 
	 * @param moduleList a list of modules
	 * @param kindList deprecated, should be null
	 */
	protected void addRemovedModules(List<IModule[]> moduleList, List<Integer> kindList) {
		server.getServerPublishInfo().addRemovedModules(moduleList);
	}

	/**
	 * Update the stored publish info for the given module.
	 * 
	 * @deprecated should never need to be called directly. Will be removed
	 *    in a future version of WTP
	 * @param deltaKind a publish delta kind
	 * @param module a module
	 */
	protected void updatePublishInfo(int deltaKind, IModule[] module) {
		// TODO remove
	}

	/**
	 * Publishes the given modules. Returns true if the publishing
	 * should continue, or false if publishing has failed or is canceled.
	 * 
	 * Uses 500 ticks plus 3500 ticks per module
	 * 
	 * @param kind the publish kind
	 * @param modules a list of modules
	 * @param deltaKind2 a list of delta kinds
	 * @param multi a multistatus to add the status to
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 */
	protected void publishModules(int kind, List modules, List deltaKind2, MultiStatus multi, IProgressMonitor monitor) {
		if (modules == null)
			return;
		
		int size = modules.size();
		if (size == 0)
			return;
		
		// publish modules
		for (int i = 0; i < size; i++) {
			if (monitor.isCanceled())
				return;
			
			// should skip this publish
			IModule[] module = (IModule[]) modules.get(i);
			IModule m = module[module.length - 1];
			if(shouldIgnorePublishRequest(m))
				continue;
			
			int kind2 = kind;
			if (getServer().getModulePublishState(module) == IServer.PUBLISH_STATE_UNKNOWN)
				kind2 = IServer.PUBLISH_FULL;
			
			/*int deltaKind = ServerBehaviourDelegate.ADDED;
			if (hasBeenPublished(module)) {
				if (getPublishedResourceDelta(module).length == 0)
					deltaKind = ServerBehaviourDelegate.NO_CHANGE;
				else
					deltaKind = ServerBehaviourDelegate.CHANGED;
			} // TODO REMOVED*/
			
			IStatus status = publishModule(kind2, module, ((Integer)deltaKind2.get(i)).intValue(), ProgressUtil.getSubMonitorFor(monitor, 3000));
			if (status != null && !status.isOK())
				multi.add(status);
		}
	}

	/**
	 * Returns whether this module should be ignore during the publish
	 * @param m
	 * @return
	 */
	protected boolean shouldIgnorePublishRequest(IModule m) {
		return (m.getProject() != null && !m.getProject().isAccessible());
	}
	
	/**
	 * Returns the publish tasks that have been targeted to this server.
	 * These tasks should be run during publishing.
	 * 
	 * @param kind one of the IServer.PUBLISH_XX constants
	 * @param moduleList a list of modules
	 * @param kindList list of one of the IServer publish change constants
	 * @return a possibly empty array of IOptionalTasks
	 */
	protected final PublishOperation[] getTasks(int kind, List moduleList, List kindList) {
		return server.getTasks(kind, moduleList, kindList);
	}

	/**
	 * Execute publishers.
	 * 
	 * @param kind the publish kind
	 * @param modules the list of modules
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @param info the IAdaptable (or <code>null</code>) provided by the
	 *    caller in order to supply UI information for prompting the
	 *    user if necessary. When this parameter is not <code>null</code>,
	 *    it should minimally contain an adapter for the
	 *    org.eclipse.swt.widgets.Shell.class
	 * @throws CoreException
	 * @since 1.1
	 * @deprecated Replaced by 
	 * {@link #executePublishers(int, List, List, IProgressMonitor, IAdaptable)}
	 */
	protected MultiStatus executePublishers(int kind, List<IModule[]> modules, IProgressMonitor monitor, IAdaptable info) throws CoreException {
		return executePublishers(kind, modules, null, monitor, info);
	}

	/**
	 * Execute publishers. If a publisher modified the contents of the module (which is determined by the
	 * {@link PublisherDelegate}) then the delta list is rebuild.
	 * 
	 * @param kind
	 *            the publish kind
	 * @param modules
	 *            the list of modules. The contents of this {@link List} may change if the publisher modifies code.
	 * @param deltaKinds
	 *            the list of delta kind that maps to the list of modules. The contents of this {@link List} may change
	 *            if the publisher modifies code.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting and cancellation are not desired
	 * @param info
	 *            the IAdaptable (or <code>null</code>) provided by the caller in order to supply UI information for
	 *            prompting the user if necessary. When this parameter is not <code>null</code>, it should minimally
	 *            contain an adapter for the org.eclipse.swt.widgets.Shell.class
	 * @throws CoreException
	 * @since 1.1
	 */
	protected MultiStatus executePublishers(int kind, List<IModule[]> modules, List<Integer> deltaKinds, IProgressMonitor monitor, IAdaptable info) throws CoreException {
		Publisher[] publishers = ((Server)getServer()).getEnabledPublishers();
		int size = publishers.length;
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Executing publishers: " + size);
		}
		
		if (size == 0)
			return null;
		
		MultiStatus multi = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, Messages.taskPerforming, null);
		
		TaskModel taskModel = new TaskModel();
		taskModel.putObject(TaskModel.TASK_SERVER, getServer());
		taskModel.putObject(TaskModel.TASK_MODULES, modules);
		if (deltaKinds != null) {
			taskModel.putObject(TaskModel.TASK_DELTA_KINDS, deltaKinds);
		}
		
		boolean publisherModifiedCode = false;
		for (int i = 0; i < size; i++) {
			Publisher pub = publishers[i];
			monitor.subTask(NLS.bind(Messages.taskPerforming, pub.getName()));
			try {
				pub.setTaskModel(taskModel);
				IStatus pubStatus = pub.execute(kind, ProgressUtil.getSubMonitorFor(monitor, 500), info);
				if(!publisherModifiedCode) {
					// If a publisher has modified modules then there is no reason to keep checking other publishers.
					publisherModifiedCode = pub.isModifyModules();
				}
				multi.add(pubStatus);
			} catch (CoreException ce) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Publisher failed", ce);
				}
				throw ce;
			}
			
			// return early if the monitor has been canceled
			if (monitor.isCanceled())
				return multi;
		}
		if (publisherModifiedCode) {
			// re-create the delta list as at least one publisher has changed the contents of the published modules.
			deltaKinds = this.computeDelta(modules);
		}
		monitor.subTask("");
		return multi;
	}

	/**
	 * Returns all the modules that are on the server, including root
	 * modules and all their children.
	 * 
	 * @return a list of IModule[]s
	 */
	protected final List<IModule[]> getAllModules() {
		return server.getAllModules();
	}

	/**
	 * Perform (execute) all the given tasks.
	 * 
	 * @param tasks an array of tasks
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return the status
	 */
	protected MultiStatus performTasks(PublishOperation[] tasks, IProgressMonitor monitor) {
		int size = tasks.length;
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Performing tasks: " + size);
		}
		
		if (size == 0)
			return null;
		
		MultiStatus multi = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, Messages.taskPerforming, null);
		
		for (int i = 0; i < size; i++) {
			PublishOperation task = tasks[i];
			monitor.subTask(NLS.bind(Messages.taskPerforming, task.toString()));
			try {
				task.execute(ProgressUtil.getSubMonitorFor(monitor, 500), null);
			} catch (CoreException ce) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Task failed", ce);
				}
				multi.add(ce.getStatus());
			}
			
			// return early if the monitor has been canceled
			if (monitor.isCanceled())
				return multi;
		}
		
		monitor.subTask("");
		return multi;
	}

	/**
	 * Called when resources change within the workspace.
	 * This gives the server an opportunity to update the server or module
	 * restart state.
	 */
	public void handleResourceChange() {
		// do nothing
	}
}