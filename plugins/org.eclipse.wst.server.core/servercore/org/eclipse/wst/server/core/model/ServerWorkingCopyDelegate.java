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

import org.eclipse.wst.server.core.IElement;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
/**
 * A server working copy delegate provides the implementation for various 
 * generic and server-type-specific operations on server working copies
 * for a specific type of server.
 * A server working copy delegate is specified by the
 * <code>workingCopyClass</code> attribute of a <code>serverTypes</code>
 * extension.
 * <p>
 * When the server working copy instance needs to be given a delegate, the
 * working copy delegate class specified for the server type is instantiated
 * with a 0-argument constructor and primed with
 * <code>delegate.initialize(serverWorkingCopy)</code>, 
 * which it is expected to hang on to.
 * Later, when <code>delegate.handleSave</code> is called,
 * the working copy is expected to [TBD]. Finally, when
 * <code>delegate.dispose()</code> is called as the server working copy is
 * being discarded, the delegate is expected to let go of the server working
 * copy.
 * </p>
 * <p>
 * Server working copy delegates may keep state in instance fields, but that
 * state is transient and will not be persisted across workbench sessions.
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
 * [issue: ServerWorkingCopyDelegate extending ServerDelegate has
 * some undesirable properties: the type has 2 initialize methods
 * public void initialize(IServerWorkingCopy workingCopy)
 * public void initialize(IServerState server).
 * It would be simpler if these types were unrelated.
 * ]
 * </p>
 * <p>
 * [issue: Since service providers must implement this class, it is
 * more flexible to provide an abstract class than an interface. It is
 * not a breaking change to add non-abstract methods to an abstract class.]
 * </p>
 * <p>
 * [issue: As mentioned on IServerWorkingCopy.getWorkingCopyDelegate(), 
 * exposing ServerWorkingCopyDelegate to clients of IServerWorkingCopy
 * is confusing and dangerous. Instead, replace IServerWorkingCopy.getWorkingCopyDelegate()
 * with something like IServerWorkingCopy.getServerWorkingCopyExtension() which
 * returns an IServerWorkingCopyExtension. The implementation of
 * IServerWorkingCopy.getServerWorkingCopyExtension() should forward to
 * getServerWorkingCopyExtension()
 * declared here. IServerWorkingCopyExtension is an * "marker" interface that
 * server providers would implement or extend only if they want to expose
 * additional API on working copies for their server type. That way 
 * ServerWorkingCopyDelegate can be kept entirely on the SPI side,
 * out of view from clients.]
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
 * @see org.eclipse.wst.server.core.IServerWorkingCopy#getWorkingCopyExtension()
 * @since 1.0
 */
public abstract class ServerWorkingCopyDelegate {
	private ServerWorkingCopy server;

	/**
	 * Initializes this server working copy delegate with its life-long server
	 * working copy.
	 * <p>
	 * This method is called by the web server core framework.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * Implementations are expected to hang on to a reference to the
	 * server working copy.
	 * </p>
	 * <p>
	 * [issue: The workingCopyClass attribute of the serverTypes extension point
	 * must stipulate that the class must have a public 0-arg constructor
	 * in addition to implementing ServerWorkingCopyDelegate.]
	 * </p>
	 * 
	 * @param workingCopy the server working copy
	 */
	public final void initialize(ServerWorkingCopy workingCopy) {
		server = workingCopy;
		initialize();
	}
	
	public void initialize() {
		// do nothing
	}

	/**
	 * Initializes this server with default values. This method is called when
	 * a new server is created so that the server can be initialized with
	 * meaningful values.
	 */
	public void setDefaults() {
		// do nothing
	}

	/**
	 * Sets the value of the specified integer-valued attribute of this
	 * element.
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see IElement#getAttribute(String, int)
	 */
	public final void setAttribute(String id, int value) {
		server.setAttribute(id, value);
	}

	/**
	 * Sets the value of the specified boolean-valued attribute of this
	 * element.
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see IElement#getAttribute(String, boolean)
	 */
	public final void setAttribute(String id, boolean value) {
		server.setAttribute(id, value);
	}

	/**
	 * Sets the value of the specified string-valued attribute of this
	 * element.
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see IElement#getAttribute(String, String)
	 */
	public final void setAttribute(String id, String value) {
		server.setAttribute(id, value);
	}

	/**
	 * Sets the value of the specified list-valued attribute of this
	 * element.
	 * <p>
	 * [issue: Serialization/deserialization]
	 * </p>
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see IElement#getAttribute(String, List)
	 */
	public final void setAttribute(String id, List value) {
		server.setAttribute(id, value);
	}

	/**
	 * Sets the value of the specified map-valued attribute of this
	 * element.
	 * <p>
	 * [issue: Serialization/deserialization]
	 * </p>
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see IElement#getAttribute(String, Map)
	 */
	public final void setAttribute(String id, Map value) {
		server.setAttribute(id, value);
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
	 *
	 * @param add a possibly-empty list of modules to add
	 * @param remove a possibly-empty list of modules to remove
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException [missing]
	 */
	public abstract void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException;

	/**
	 * Handles a save of this server working copy. This method is called
	 * when the server working copy <code>save</code> method
	 * is invoked and can be used to resolve calculated fields or perform
	 * other operations related to the changes that are being made.
	 * <p>
	 * This method is called by the web server core framework.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * [issue: It's unclear why this method is necessary.]
	 * </p>
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 */
	public abstract void handleSave(IProgressMonitor monitor);
	
	/**
	 * Disposes of this runtime delegate.
	 * <p>
	 * This method is called by the web server core framework.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * Implementations are expected to let go of the delegate's reference
	 * to the runtime, deregister listeners, etc.
	 * </p>
	 */
	public void dispose() {
		// do nothing
	}
}