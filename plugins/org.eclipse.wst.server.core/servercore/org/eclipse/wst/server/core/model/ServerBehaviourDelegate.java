/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerPlugin;
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
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @see IServer
 * @see IServerWorkingCopy
 * @since 1.0
 */
public abstract class ServerBehaviourDelegate {
	private Server server;

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
	 * @param server the server instance
	 */
	public final void initialize(Server newServer) {
		server = newServer;
		initialize();
	}

	/**
	 * Initializes this server delegate. This method gives delegates a chance
	 * to do their own initialization.
	 * <p>
	 * This method is called by the server core framework.
	 * Clients should never call this method.
	 * </p>
	 */
	public void initialize() {
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
	 * @param state one of the server state (<code>STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 * @see IServer#getServerState()
	 */
	public final void setServerState(int state) {
		server.setServerState(state);
	}

	/**
	 * Sets the ILaunchManager mode that the server is running in. The server
	 * implementation will automatically return <code>null</code> to clients
	 * when the server is stopped, so you only need to update the mode when
	 * it changes.
	 * 
	 * @param mode the mode in which a server is running, one of the mode constants
	 *    defined by {@link org.eclipse.debug.core.ILaunchManager}
	 */
	public final void setMode(String mode) {
		server.setMode(mode);
	}

	/**
	 * 
	 * @param modules
	 */
	public final void setModules(IModule[] modules) {
		server.setModules(modules);
	}

	/**
	 * Sets the server restart state.
	 *
	 * @param state boolean
	 */
	public final void setServerRestartState(boolean state) {
		server.setServerRestartState(state);
	}

	/**
	 * Sets the server publish state.
	 *
	 * @param state int
	 */
	public final void setServerPublishState(int state) {
		server.setServerPublishState(state);
	}

	/**
	 * Hook to fire an event when a module state changes.
	 * 
	 * @param module
	 * @param state
	 */
	public final void setModuleState(IModule module, int state) {
		server.setModuleState(module, state);
	}

	/**
	 * Sets the module publish state.
	 *
	 * @param state int
	 */
	public final void setModulePublishState(IModule module, int state) {
		server.setModulePublishState(module, state);
	}
	
	/**
	 * Sets the module restart state.
	 *
	 * @param state int
	 */
	public final void setModuleRestartState(IModule module, boolean state) {
		server.setModuleRestartState(module, state);
	}

	/**
	 * Disposes of this server delegate.
	 * <p>
	 * This method is called by the web server core framework.
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
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @throws CoreException
	 */
	public void publishStart(IProgressMonitor monitor) throws CoreException {
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
	 *    <li><code>PUBLSIH_FULL</code>- indicates a full publish.</li>
	 *    <li><code>PUBLISH_INCREMENTAL</code>- indicates a incremental publish.
	 *    <li><code>PUBLSIH_AUTO</code>- indicates an automatic incremental publish.</li>
	 *    <li><code>PUBLISH_CLEAN</code>- indicates a clean request. Clean throws
	 *      out all state and cleans up the module on the server before doing a
	 *      full publish.
	 *    </ul>
	 * @param monitor
	 * @throws CoreException
	 */
	public abstract void publishServer(int kind, IProgressMonitor monitor) throws CoreException;

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
	 * 
	 * @param kind one of the IServer.PUBLISH_XX constants. Valid values are
	 *    <ul>
	 *    <li><code>PUBLSIH_FULL</code>- indicates a full publish.</li>
	 *    <li><code>PUBLISH_INCREMENTAL</code>- indicates a incremental publish.
	 *    <li><code>PUBLSIH_AUTO</code>- indicates an automatic incremental publish.</li>
	 *    <li><code>PUBLISH_CLEAN</code>- indicates a clean request. Clean throws
	 *      out all state and cleans up the module on the server before doing a
	 *      full publish.
	 *    </ul>
	 * @param parents the parent modules of this module
	 * @param module the module to publish
	 * @param deltaKind one of the IServer publish change constants. Valid values are
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
	 * @param monitor
	 * @throws CoreException
	 */
	public abstract void publishModule(int kind, int deltaKind, IModule[] parents, IModule module, IProgressMonitor monitor) throws CoreException;

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
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @throws CoreException
	 */
	public void publishFinish(IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Configure the given launch configuration to start this server. This method is called whenever
	 * the server is started to ensure that the launch configuration is accurate and up to date.
	 * This method should not blindly update the launch configuration in cases where the user has
	 * access to change the launch configuration by hand.
	 * 
	 * @param workingCopy
	 * @param monitor
	 * @throws CoreException
	 */
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Restart this server. The server should use the server
	 * listener to notify progress. It must use the same debug
	 * flags as was originally passed into the start() method.
	 * 
	 * This method is used if there is a quick/better way to restart
	 * the server. If it throws a CoreException, the normal stop/start
	 * actions will be used.
	 */
	public void restart(String launchMode) throws CoreException {
		 throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, "Could not restart", null));
	}

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
	public boolean canRestartModule(IModule module) {
		return false;
	}

	/**
	 * Asynchronously restarts the given module on the server.
	 * See the specification of 
	 * {@link IServer#synchronousRestartModule(IModule, IProgressMonitor)}
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
	 * [issue: Since this method is ascynchronous, is there
	 * any need for the progress monitor?]
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
	 * @exception CoreException if an error occurs while trying to restart the module
	 */
	public void restartModule(IModule module, IProgressMonitor monitor) throws CoreException {
		// do nothing
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
	 * should be a mechanism that allows failing asynch operations
	 * to be diagnosed.]
	 * </p>
	 * @param force <code>true</code> to kill the server, or <code>false</code>
	 *    to stop normally
	 */
	public abstract void stop(boolean force);

	/**
	 * Returns the module resources that have been published to the server.
	 * 
	 * <p>
	 * If the module has just been added to the server, an empty list will
	 * be returned. If the module has never existed on the server, a CoreException
	 * will be thrown.
	 * </p>
	 * 
	 * @param parents
	 * @param module
	 * @return an array containing the published module resource
	 */
	public IModuleResource[] getPublishedResources(IModule[] parents, IModule module) {
		return server.getPublishedResources(parents, module);
	}

	/**
	 * Returns the delta of the current module resources that have been
	 * published compared to the current state of the module.
	 *
	 * @param parents
	 * @param module
	 * @return an array containing the publish resource delta
	 */
	public IModuleResourceDelta[] getPublishedResourceDelta(IModule[] parents, IModule module) {
		return server.getPublishedResourceDelta(parents, module);
	}
	
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
	public IPath getTempDirectory() {
		return server.getTempDirectory();
	}
}