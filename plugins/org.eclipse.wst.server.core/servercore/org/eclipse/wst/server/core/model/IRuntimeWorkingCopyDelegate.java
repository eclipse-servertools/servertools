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

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
/**
 * A runtime working copy delegate provides the implementation for various 
 * generic and runtime-type-specific operations on runtime working copies
 * for a specific type of runtime.
 * A runtime working copy delegate is specified by the
 * <code>workingCopyClass</code> attribute of a <code>runtimeTypes</code>
 * extension.
 * <p>
 * When the runtime working copy instance needs to be given a delegate, the
 * working copy delegate class specified for the runtime type is instantiated
 * with a 0-argument constructor and primed with
 * <code>delegate.initialize(runtime)</code>, 
 * which it is expected to hang on to.
 * Later, when <code>delegate.handleSave</code> is called,
 * the working copy is expected to [TBD]. Finally, when
 * <code>delegate.dispose()</code> is called as the runtime working copy is
 * being discarded, the delegate is expected to let go of the runtime working
 * copy.
 * </p>
 * <p>
 * Runtime working copy delegates may keep state in instance fields, but that
 * state is transient and will not be persisted across workbench sessions.
 * </p>
 * <p>
 * [issue: Runtime delegates can read runtime attributes via
 * IRuntime; runtime working copy delegates can also set them
 * via IRuntimeWorkingCopy. However, current implementation does
 * not serialize any attributes other than the ones server core
 * knows about. So it's unclear whether there is any intent
 * to support attributes that are runtime-type-specific.]
 * </p>
 * <p>
 * [issue: IRuntimeWorkingCopyDelegate extending IRuntimeDelegate has
 * some undesirable properties: the type has 2 initialize methods
 * public void initialize(IRuntimeWorkingCopy workingCopy)
 * public void initialize(IRuntime runtime).
 * It would be simpler if these types were unrelated.
 * ]
 * </p>
 * <p>
 * [issue: Since service providers must implement this class, it is
 * more flexible to provide an abstract class than an interface. It is
 * not a breaking change to add non-abstract methods to an abstract class.]
 * </p>
 * <p>
 * [issue: As mentioned on IRuntimeWorkingCopy.getWorkingCopyDelegate(), 
 * exposing IRuntimeWorkingCopyDelegate to clients of IRuntimeWorkingCopy
 * is confusing and dangerous. Instead, replace IRuntimeWorkingCopy.getWorkingCopyDelegate()
 * with something like IRuntimeWorkingCopy.getRuntimeWorkingCopyExtension() which
 * returns an IRuntimeWorkingCopyExtension. The implementation of
 * IRuntimeWorkingCopy.getRuntimeWorkingCopyExtension() should forward to
 * getRuntimeWorkingCopyExtension()
 * declared here. IRuntimeWorkingCopyExtension is an * "marker" interface that
 * runtime providers would implement or extend only if they want to expose
 * additional API on working copies for their runtime type. That way 
 * IRuntimeWorkingCopyDelegate can be kept entirely on the SPI side,
 * out of view from clients.]
 * </p>
 * <p>
 * This interface is intended to be implemented only by clients
 * to extend the <code>runtimeTypes</code> extension point.
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @see org.eclipse.wst.server.core.IServerWorkingCopy#getWorkingCopyDelegate()
 * @since 1.0
 */
public interface IRuntimeWorkingCopyDelegate extends IRuntimeDelegate {
	/**
	 * Constant (value 0) indicating the pre-save call to 
	 * {@link #handleSave(byte, IProgressMonitor)}.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * <p>
	 * [issue: same constant is duplicated on IServerWorkingCopyDelegate.]
	 * </p>
	 */
	public static final byte PRE_SAVE = 0;
	
	/**
	 * Constant (value 1) indicating the post-save call to 
	 * {@link #handleSave(byte, IProgressMonitor)}.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 * <p>
	 * [issue: same constant is duplicated on IServerWorkingCopyDelegate.]
	 * </p>
	 */
	public static final byte POST_SAVE = 1;

	/**
	 * Initializes this runtime working copy delegate with its life-long server
	 * working copy.
	 * <p>
	 * This method is called by the web server core framework.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * Implementations are expected to hang on to a reference to the
	 * runtime working copy.
	 * </p>
	 * <p>
	 * [issue: The workingCopyClass attribute of the runtimeTypes extension point
	 * must stipulate that the class must have a public 0-arg constructor
	 * in addition to implementing IRuntimeWorkingCopyDelegate.]
	 * </p>
	 * 
	 * @param workingCopy the runtime working copy
	 */
	public void initialize(IRuntimeWorkingCopy workingCopy);
	
	/**
	 * <p>
	 * [issue: Who would call this method, and what does it do?]
	 * </p>
	 */
	public void setDefaults();
	
	/**
	 * Handles a save of this runtime working copy. This method is called
	 * when the runtime working copy <code>save</code> method
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