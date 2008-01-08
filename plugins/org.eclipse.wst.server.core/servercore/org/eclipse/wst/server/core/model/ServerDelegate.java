/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
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
 * ServerDelegate supports an open-ended set of attribute-value pairs. All
 * state stored in this manner will be saved when the server working copy is
 * saved, and persisted across workbench sessions.
 * Server delegates may keep state in instance fields, but that state is
 * transient and will not be persisted across workbench sessions. To save
 * state across workbench sessions, it must be persisted using the
 * attributes.
 * </p>
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>serverTypes</code> extension point.
 * </p>
 * 
 * @see IServer
 * @see IServerWorkingCopy
 * @since 1.0
 */
public abstract class ServerDelegate {
	private Server server;
	private ServerWorkingCopy serverWC;

	/**
	 * Delegates must have a public 0-arg constructor.
	 */
	public ServerDelegate() {
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
		if (newServer instanceof ServerWorkingCopy)
			serverWC = (ServerWorkingCopy) newServer;
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
	protected void initialize() {
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
	 * Returns the server working copy that this server delegate corresponds to.
	 * 
	 * @return the server
	 */
	public final IServerWorkingCopy getServerWorkingCopy() {
		return serverWC;
	}

	/**
	 * Returns the value of the specified int-valued attribute.
	 * 
	 * @param id the attribute id
	 * @param defaultValue the default value of the specified attribute
	 * @return the attribute value
	 * @see #setAttribute(String, int)
	 */
	protected final int getAttribute(String id, int defaultValue) {
		return server.getAttribute(id, defaultValue);
	}

	/**
	 * Returns the value of the specified boolean-valued attribute.
	 * 
	 * @param id the attribute id
	 * @param defaultValue the default value of the specified attribute
	 * @return the attribute value
	 * @see #setAttribute(String, boolean)
	 */
	protected final boolean getAttribute(String id, boolean defaultValue) {
		return server.getAttribute(id, defaultValue);
	}

	/**
	 * Returns the value of the specified String-valued attribute.
	 * 
	 * @param id the attribute id
	 * @param defaultValue the default value of the specified attribute
	 * @return the attribute value
	 * @see #setAttribute(String, String)
	 */
	protected final String getAttribute(String id, String defaultValue) {
		return server.getAttribute(id, defaultValue);
	}

	/**
	 * Returns the value of the specified List-valued attribute.
	 * 
	 * @param id the attribute id
	 * @param defaultValue the default value of the specified attribute
	 * @return the attribute value
	 * @see #setAttribute(String, List)
	 */
	protected final List getAttribute(String id, List defaultValue) {
		return server.getAttribute(id, defaultValue);
	}

	/**
	 * Returns the value of the specified Map-valued attribute.
	 * 
	 * @param id the attribute id
	 * @param defaultValue the default value of the specified attribute
	 * @return the attribute value
	 * @see #setAttribute(String, Map)
	 */
	protected final Map getAttribute(String id, Map defaultValue) {
		return server.getAttribute(id, defaultValue);
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
	 * Returns whether the specified module modifications could be made to this
	 * server at this time. See the specification of
	 * {@link IServerAttributes#canModifyModules(IModule[], IModule[], IProgressMonitor)}
	 * for further details. 
	 * <p>
	 * This method is called by the web server core framework in response to
	 * a call to <code>IServer.canModifyModules</code>. It should return quickly
	 * without connection to the server. Clients should never call this method.
	 * </p>
	 *
	 * @param add a possibly-empty list of modules to add
	 * @param remove a possibly-empty list of modules to remove
	 * @return a status object with code <code>IStatus.OK</code> if the modules
	 *   can be modified, otherwise a status object indicating why they can't
	 * @see IServerAttributes#canModifyModules(IModule[], IModule[], IProgressMonitor)
	 */
	public abstract IStatus canModifyModules(IModule[] add, IModule[] remove);

	/**
	 * Returns the child module(s) of this module. If this module contains other
	 * modules, it should list those modules. If not, it should return an empty
	 * list.
	 * 
	 * <p>This method should only return the direct children. To obtain the full
	 * tree of modules if they are multiple levels deep, this method may be
	 * recursively called on the children.</p>
	 * 
	 * @param module a module
	 * @return the child modules
	 * @see IServerAttributes#getChildModules(IModule[], IProgressMonitor)
	 */
	public abstract IModule[] getChildModules(IModule[] module);

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
	 * [issue: should the parameter be IModule[]?]
	 * 
	 * @param module a module
	 * @return an array of possible root modules
	 * @throws CoreException if anything went wrong
	 * @see org.eclipse.wst.server.core.IServerAttributes#getRootModules(IModule, IProgressMonitor)
	 */
	public abstract IModule[] getRootModules(IModule module) throws CoreException;

	/**
	 * Returns an array of ServerPorts that this server has.
	 *
	 * @return the server's ports
	 */
	public ServerPort[] getServerPorts() {
		return null;
	}

	/**
	 * Initializes this server with default values. This method is called when
	 * a new server is created so that the server can be initialized with
	 * meaningful values.
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 */
	public void setDefaults(IProgressMonitor monitor) {
		// do nothing
	}
	
	/**
	 * Sets the value of the specified integer-valued attribute of this
	 * element.
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see #getAttribute(String, int)
	 */
	protected final void setAttribute(String id, int value) {
		serverWC.setAttribute(id, value);
	}

	/**
	 * Sets the value of the specified boolean-valued attribute of this
	 * element.
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see #getAttribute(String, boolean)
	 */
	protected final void setAttribute(String id, boolean value) {
		serverWC.setAttribute(id, value);
	}

	/**
	 * Sets the value of the specified string-valued attribute of this
	 * element.
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see #getAttribute(String, String)
	 */
	protected final void setAttribute(String id, String value) {
		serverWC.setAttribute(id, value);
	}

	/**
	 * Sets the value of the specified list-valued attribute of this
	 * element. The list may only contain String values.
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see #getAttribute(String, List)
	 */
	protected final void setAttribute(String id, List value) {
		serverWC.setAttribute(id, value);
	}

	/**
	 * Sets the value of the specified map-valued attribute of this
	 * element. The map may only contain String values.
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see #getAttribute(String, Map)
	 */
	protected final void setAttribute(String id, Map value) {
		serverWC.setAttribute(id, value);
	}

	/**
	 * Modifies the list of modules associated with the server.
	 * See the specification of
	 * {@link IServerWorkingCopy#modifyModules(IModule[], IModule[], IProgressMonitor)}
	 * for further details.
	 * <p>
	 * This method is called by the web server core framework,
	 * in response to a call to <code>IServerWorkingCopy.modifyModules</code>.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * This method is called to update the server configuration (if any)
	 * or update the delegates internal state. Note that the actual list
	 * of modules is stored on the server and can be accessed at any time
	 * using server.getModules(). getModules() will not be updated until
	 * after this method successfully returns.
	 * </p>
	 * <p>
	 * This method will not communicate with the server. After saving,
	 * publish() can be used to sync up with the server.
	 * </p>
	 *
	 * @param add a possibly-empty list of modules to add
	 * @param remove a possibly-empty list of modules to remove
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if the changes are not allowed or could not
	 *    be processed
	 */
	public abstract void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException;

	/**
	 * This method is called to import the server configuration from the given
	 * runtime.
	 * 
	 * @param runtime a server runtime
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @deprecated should use importRuntimeConfiguration (which can throw a
	 *    CoreException) instead
	 */
	public void importConfiguration(IRuntime runtime, IProgressMonitor monitor) {
		try {
			importRuntimeConfiguration(runtime, monitor);
		} catch (CoreException ce) {
			// ignore
		}
	}

	/**
	 * This method is called to import the server configuration from the given
	 * runtime.
	 * 
	 * @param runtime a server runtime
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is any problem importing the configuration
	 *    from the runtime
	 */
	public void importRuntimeConfiguration(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * This method is called whenever the server configuration should be saved.
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there was a problem saving
	 */
	public void saveConfiguration(IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * This method is called whenever the server configuration folder has changed.
	 * It gives the server a chance to throw out any old data and be ready to
	 * reload the server configuration when it is needed next.
	 */
	public void configurationChanged() {
		// do nothing
	}
}