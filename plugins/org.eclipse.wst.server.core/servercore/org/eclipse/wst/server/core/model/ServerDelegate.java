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
package org.eclipse.wst.server.core.model;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerPort;
import org.eclipse.wst.server.core.internal.Server;
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
 * [issue: Server delegates can read server attributes via
 * IServer; server working copy delegates can also set them
 * via IServerWorkingCopy. However, current implementation does
 * not serialize any attributes other than the ones server core
 * knows about. So it's unclear whether there is any intent
 * to support attributes that are server-type-specific.]
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
 * @see IServer#getExtension()
 * @since 1.0
 */
public abstract class ServerDelegate {
	private Server server;
	
	public ServerDelegate() {
		// do nothing
	}

	/**
	 * Initializes this server delegate with its life-long server instance.
	 * <p>
	 * This method is called by the server core framework.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * [issue: The class attribute of the serverTypes extension point
	 * must stipulate that the class must have a public 0-arg constructor
	 * in addition to implementing ServerDelegate.]
	 * </p>
	 * 
	 * @param server the server instance
	 */
	public final void initialize(Server newServer) {
		server = newServer;
		initialize();
	}

	public void initialize() {
		// do nothing
	}

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
	
	public final void setMode(String mode) {
		server.setMode(mode);
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

	public final int getAttribute(String attributeName, int defaultValue) {
		return server.getAttribute(attributeName, defaultValue);
	}

	public final boolean getAttribute(String attributeName, boolean defaultValue) {
		return server.getAttribute(attributeName, defaultValue);
	}
	
	public final String getAttribute(String attributeName, String defaultValue) {
		return server.getAttribute(attributeName, defaultValue);
	}

	public final List getAttribute(String attributeName, List defaultValue) {
		return server.getAttribute(attributeName, defaultValue);
	}

	public final Map getAttribute(String attributeName, Map defaultValue) {
		return server.getAttribute(attributeName, defaultValue);
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
	 * The server configuration has changed. This method should return
	 * quickly. If any republishing must occur, the relevant in-sync
	 * methods should return a new value. If the server must be restarted,
	 * the isRestartNeeded() method should return true.
	 * 
	 * @see IServer#updateConfiguration()
	 */
	//public abstract void updateConfiguration();

	/**
	 * A module resource has changed. This method should return
	 * quickly. If the server must be restarted to handle the
	 * change of this file, the isRestartNeeded() method should
	 * return true and the event should be fired.
	 *
	 * @param module org.eclipse.wst.server.core.IModule
	 * @param delta org.eclipse.wst.server.core.IModuleResourceDelta
	 */
	//public abstract void updateModule(IModule module, IModuleResourceDelta delta);

	/**
	 * Methods called to notify that publishing is about to begin.
	 * This allows the server to open a connection to the server
	 * or get any global information ready.
	 *
	 * <p>This method should not be called directly! Use the
	 * IServerControl to correctly publish to the server.</p>
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public void publishStart(IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Publish the configuration.
	 * 
	 * @param monitor
	 * @return
	 */
	public abstract void publishServer(IProgressMonitor monitor) throws CoreException;

	/**
	 * Publish an individual module to the server.
	 * 
	 * @param parents
	 * @param module
	 * @return
	 */
	public abstract void publishModule(IModule[] parents, IModule module, IProgressMonitor monitor) throws CoreException;

	/**
	 * Methods called to notify that publishing has finished.
	 * The server can close any open connections to the server
	 * and do any cleanup operations.
	 *
	 * <p>This method should not be called directly! Use the
	 * IServerControl to correctly publish to the
	 * server.</p>
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public void publishStop(IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Returns whether the specified module modifications could be made to this
	 * server at this time. See the specification of
	 * {@link IServer#canModifyModules(IModule[], IModule[])}
	 * for further details. 
	 * <p>
	 * This method is called by the web server core framework,
	 * in response to a call to <code>IServer.canModifyModules</code>.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * [issue: See IServer.canModifyModules(IModule[], IModule[]).]
	 * </p>
	 *
	 * @param add a possibly-empty list of modules to add
	 * @param remove a possibly-empty list of modules to remove
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return <code>true</code> if the proposed modifications
	 * look feasible, and <code>false</code> otherwise
	 * Returns true if this module can be added to this
	 * configuration at the current time, and false otherwise.
	 */
	public abstract IStatus canModifyModules(IModule[] add, IModule[] remove);

	/**
	 * Returns the list of modules that are associated with
	 * this server. See the specification of
	 * {@link IServer#getModules()} for further details. 
	 * <p>
	 * This method is called by the web server core framework,
	 * in response to a call to <code>IServer.getModules</code>.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * [issue: Where does the delegate get these objects from,
	 * especially at the start of a follow-on session?]
	 * </p>
	 *
	 * @return a possibly-empty list of modules
	 */
	public abstract IModule[] getModules();

	/**
	 * Returns the child module(s) of this module. If this
	 * module contains other modules, it should list those
	 * modules. If not, it should return an empty list.
	 *
	 * <p>This method should only return the direct children.
	 * To obtain the full module tree, this method may be
	 * recursively called on the children.</p>
	 *
	 * @see IServer#getChildModules(IModule)
	 */
	public abstract IModule[] getChildModules(IModule module);

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
	 * @see IServer#getParentModules(IModule)
	 */
	public abstract IModule[] getParentModules(IModule module) throws CoreException;
	
	/**
	 * 
	 * @see IServer#setLaunchDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public abstract void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy);

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
		 throw new CoreException(null);
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
	 * Check if the given module is in sync on the server. It should
	 * return true if the module should be restarted (is out of
	 * sync) or false if the module does not need to be restarted.
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return boolean
	 */
	/*public boolean isModuleRestartNeeded(IModule module) {
		return false;
	}*/

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
	 * [issue: It should probably be spec'd to throw an exception error if the
	 * given module is not associated with the server.]
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
	public void restartModule(IModule module, IProgressMonitor monitor) throws CoreException {
		// do nothing
	}
	
	/**
	 * Cleanly shuts down and stops this server. The
	 * server should return from this method quickly and
	 * use the server listener to notify shutdown progress.
	 *
	 * <p>This method should not be called directly! Use the
	 * IServer to correctly start and register
	 * the server.</p>
	 */
	public abstract void stop();

	/**
	 * Terminate the server process(es). This method should only be
	 * used as a last resort after the stop() method fails to work.
	 * The server should return from this method quickly and
	 * use the server listener to notify shutdown progress.
	 * It MUST terminate the server completely and return it to
	 * the stopped state.
	 */
	public abstract void terminate();
	
	/**
	 * Returns an array of IServerPorts that this server has.
	 *
	 * @return
	 */
	public IServerPort[] getServerPorts() {
		return null;
	}
}