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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.IServerWorkingCopy;
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
 * [issue: IServerWorkingCopyDelegate extending IServerDelegate has
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
 * exposing IServerWorkingCopyDelegate to clients of IServerWorkingCopy
 * is confusing and dangerous. Instead, replace IServerWorkingCopy.getWorkingCopyDelegate()
 * with something like IServerWorkingCopy.getServerWorkingCopyExtension() which
 * returns an IServerWorkingCopyExtension. The implementation of
 * IServerWorkingCopy.getServerWorkingCopyExtension() should forward to
 * getServerWorkingCopyExtension()
 * declared here. IServerWorkingCopyExtension is an * "marker" interface that
 * server providers would implement or extend only if they want to expose
 * additional API on working copies for their server type. That way 
 * IServerWorkingCopyDelegate can be kept entirely on the SPI side,
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
 * @see org.eclipse.wst.server.core.IServerWorkingCopy#getWorkingCopyDelegate()
 * @since 1.0
 */
public interface IServerWorkingCopyDelegate extends IServerDelegate {
	
	/**
	 * Constant (value 0) indicating the pre-save call to 
	 * {@link #handleSave(byte, IProgressMonitor)}.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 */
	public static final byte PRE_SAVE = 0;
	
	/**
	 * Constant (value 1) indicating the post-save call to 
	 * {@link #handleSave(byte, IProgressMonitor)}.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 */
	public static final byte POST_SAVE = 1;

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
	 * in addition to implementing IServerWorkingCopyDelegate.]
	 * </p>
	 * 
	 * @param workingCopy the server working copy
	 */
	public void initialize(IServerWorkingCopy workingCopy);

	/**
	 * <p>
	 * [issue: Who would call this method, and what does it do?]
	 * </p>
	 */
	public void setDefaults();

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
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException;

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
	 * <p>
	 * [issue: Explain how PRE_SAVE and POST_SAVE fit in.]
	 * </p>
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.
	 * Or, in this case, a boolean.]
	 * </p>
	 * 
	 * @param id one of {@link #PRE_SAVE} or {@link #POST_SAVE}
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 */
	public void handleSave(byte id, IProgressMonitor monitor);
}