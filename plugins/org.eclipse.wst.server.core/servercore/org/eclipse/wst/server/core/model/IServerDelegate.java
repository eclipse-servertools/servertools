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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerState;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.resources.IModuleResourceDelta;
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
 * [issue: Since service providers must implement this class, it is
 * more flexible to provide an abstract class than an interface. It is
 * not a breaking change to add non-abstract methods to an abstract class.]
 * </p>
 * <p>
 * [issue: As mentioned on IServer.getDelegate(), 
 * exposing IServerDelegate to clients of IServer
 * is confusing and dangerous. Instead, replace IServer.getDelegate()
 * with something like IServer.getServerExtension() which
 * returns an IServerExtension. The implementation of
 * IServer.getServerExtension() should forward to getServerExtension()
 * declared here. IServerExtension is an * "marker" interface that
 * server providers would implement or extend only if they want to expose
 * additional API for their server type. That way IServerDelegate
 * can be kept entirely on the SPI side, out of view from 
 * clients.]
 * </p>
 * <p>
 * This interface is intended to be implemented only by clients
 * to extend the <code>serverTypes</code> extension point.
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @see IServer#getDelegate()
 * @since 1.0
 */
public interface IServerDelegate {
	
	/**
	 * Initializes this server delegate with its life-long server instance.
	 * <p>
	 * This method is called by the web server core framework.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * Implementations are expected to hang on to a reference to the server.
	 * </p>
	 * <p>
	 * [issue: The class attribute of the serverTypes extension point
	 * must stipulate that the class must have a public 0-arg constructor
	 * in addition to implementing IServerDelegate.]
	 * </p>
	 * 
	 * @param server the server instance
	 */
	public void initialize(IServerState server);

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
	public void dispose();
	
	/**
	 * Returns the publisher that can be used to publish the
	 * given module to this server.
	 * <p>
	 * [issue: Although getPublisher is also found on
	 * on IServer, it probably does not belong there. Here
	 * on the SPI side is where it makes sense: it allows the
	 * IPublisher implementation to be determined (and implemented)
	 * in a server-type-specific manner.]
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
	 * The server configuration has changed. This method should return
	 * quickly. If any republishing must occur, the relevant in-sync
	 * methods should return a new value. If the server must be restarted,
	 * the isRestartNeeded() method should return true.
	 * 
	 * @see IServer#updateConfiguration()
	 */
	public void updateConfiguration();

	/**
	 * A module resource has changed. This method should return
	 * quickly. If the server must be restarted to handle the
	 * change of this file, the isRestartNeeded() method should
	 * return true and the event should be fired.
	 *
	 * @param module org.eclipse.wst.server.core.IModule
	 * @param delta org.eclipse.wst.server.core.IModuleResourceDelta
	 */
	public void updateModule(IModule module, IModuleResourceDelta delta);

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
	public IStatus publishStart(IProgressMonitor monitor);

	/**
	 * Publish the configuration.
	 * 
	 * @param monitor
	 * @return
	 */
	public IStatus publishConfiguration(IProgressMonitor monitor);

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
	public IStatus publishStop(IProgressMonitor monitor);
	
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
	public IStatus canModifyModules(IModule[] add, IModule[] remove);

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
	public IModule[] getModules();
	
	/**
	 * Returns the current state of the given module on this server.
	 * Returns <code>MODULE_STATE_UNKNOWN</code> if the module
	 * is not among the ones associated with this server.
	 * See the specification of {@link IServer#getModuleState(IModule)}
	 * for further details. 
	 * <p>
	 * This method is called by the web server core framework,
	 * in response to a call to <code>IServer.getModuleState</code>.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * [issue: It's unclear whether this operations is guaranteed to be fast
	 * or whether it could involve communication with any actual
	 * server. If it is not fast, the method should take a progress
	 * monitor.]
	 * </p>
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 *
	 * @param module the module
	 * @return one of the module state (<code>MODULE_STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 */
	public byte getModuleState(IModule module);

	/**
	 * Method called when changes to the modules or module factories
	 * within this configuration occur. Return any necessary commands to repair
	 * or modify the server configuration in response to these changes.
	 * 
	 * @see IServer#getRepairCommands(IModuleFactoryEvent[], IModuleEvent[])
	 */
	public ITask[] getRepairCommands(IModuleFactoryEvent[] factoryEvent, IModuleEvent[] moduleEvent);
	
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
	 * @see IServer#getParentModules(IModule)
	 */
	public List getParentModules(IModule module) throws CoreException;
	
	/**
	 * 
	 * @see IServer#setLaunchDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy);
}
