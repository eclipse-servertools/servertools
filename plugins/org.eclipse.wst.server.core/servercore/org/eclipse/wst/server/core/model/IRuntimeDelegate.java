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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IRuntime;
/**
 * A runtime delegate provides the implementation for various 
 * generic and server-type-specific operations for a specific type of runtime.
 * A runtime delegate is specified by the
 * <code>class</code> attribute of a <code>runtimeTypes</code> extension.
 * <p>
 * When the runtime instance needs to be given a delegate, the delegate class
 * specified for the runtime type is instantiated with a 0-argument constructor
 * and primed with <code>delegate.initialize(runtime)</code>, 
 * which it is expected to hang on to. Later, when
 * <code>delegate.dispose()</code> is called as the runtime instance is
 * being discarded, the delegate is expected to let go of the runtime instance.
 * </p>
 * <p>
 * Runtime delegates may keep state in instance fields, but that state is
 * transient and will not be persisted across workbench sessions.
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
 * [issue: Since service providers must implement this class, it is
 * more flexible to provide an abstract class than an interface. It is
 * not a breaking change to add non-abstract methods to an abstract class.]
 * </p>
 * <p>
 * [issue: As mentioned on IRuntime.getDelegate(), 
 * exposing IRuntimeDelegate to clients of IRuntime
 * is confusing and dangerous. Instead, replace IRuntime.getDelegate()
 * with something like IRuntime.getRuntimeExtension() which
 * returns an IRuntimeExtension. The implementation of
 * IRuntime.getRuntimeExtension() should forward to getRuntimeExtension()
 * declared here. IRuntimeExtension is an * "marker" interface that
 * runtime providers would implement or extend only if they want to expose
 * additional API for their runtime type. That way IRuntimeDelegate
 * can be kept entirely on the SPI side, out of view from 
 * clients.]
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
 * @see IRuntime#getDelegate()
 * @since 1.0
 */
public interface IRuntimeDelegate {
	
	/**
	 * Initializes this runtime delegate with its life-long runtime instance.
	 * <p>
	 * This method is called by the web server core framework.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * Implementations are expected to hang on to a reference to the runtime.
	 * </p>
	 * <p>
	 * [issue: The class attribute of the runtimeTypes extension point
	 * must stipulate that the class must have a public 0-arg constructor
	 * in addition to implementing IRuntimeDelegate.]
	 * </p>
	 * 
	 * @param runtime the runtime instance
	 */
	public void initialize(IRuntime runtime);

	/**
	 * Validates this runtime instance. See the specification of
	 * {@link IRuntime#validate()} for further details. 
	 * <p>
	 * This method is called by the web server core framework,
	 * in response to a call to <code>IRuntime.validate()</code>.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * [issue: see issues flagged on IRuntime.validate().]
	 * </p>
	 *
	 * @return a status object with code <code>IStatus.OK</code> if this
	 * runtime is valid, otherwise a status object indicating what is
	 * wrong with it
	 */
	public IStatus validate();

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
	public void dispose();
}