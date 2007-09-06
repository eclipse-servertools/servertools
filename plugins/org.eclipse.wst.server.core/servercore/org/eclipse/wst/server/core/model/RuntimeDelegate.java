/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Messages;
import org.eclipse.wst.server.core.internal.Runtime;
import org.eclipse.wst.server.core.internal.RuntimeWorkingCopy;
import org.eclipse.wst.server.core.internal.ServerPlugin;
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
 * RuntimeDelegate supports an open-ended set of attribute-value pairs. All
 * state stored in this manner will be saved when the runtime working copy is
 * saved, and persisted across workbench sessions.
 * Runtime delegates may keep state in instance fields, but that state is
 * transient and will not be persisted across workbench sessions. To save state
 * across workbench sessions, it must be persisted using the attributes.
 * </p>
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>runtimeTypes</code> extension point.
 * </p>
 * 
 * @see IRuntime
 * @see IRuntimeWorkingCopy
 * @since 1.0
 */
public abstract class RuntimeDelegate {
	private Runtime runtime;
	private RuntimeWorkingCopy runtimeWC;
	
	/**
	 * Delegates must have a public 0-arg constructor.
	 */
	public RuntimeDelegate() {
		// do nothing
	}

	/**
	 * Initializes this runtime delegate with its life-long runtime instance.
	 * <p>
	 * This method is called by the server core framework.
	 * Clients should never call this method.
	 * </p>
	 * 
	 * @param newRuntime the runtime instance
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 */
	final void initialize(Runtime newRuntime, IProgressMonitor monitor) {
		runtime = newRuntime;
		if (runtime instanceof RuntimeWorkingCopy)
			runtimeWC = (RuntimeWorkingCopy) runtime;
		initialize();
	}

	/**
	 * Initializes this runtime delegate. This method gives delegates a chance
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
	 * Returns the runtime that this runtime delegate corresponds to.
	 * 
	 * @return the runtime
	 */
	public final IRuntime getRuntime() {
		return runtime;
	}

	/**
	 * Returns the runtime working copy that this runtime delegate corresponds to.
	 * 
	 * @return the runtime
	 */
	public final IRuntimeWorkingCopy getRuntimeWorkingCopy() {
		return runtimeWC;
	}

	/**
	 * Validates this runtime instance. Subclasses should
	 * override and call super.validate() for basic validation. 
	 * <p>
	 * This method is called by the web server core framework,
	 * in response to a call to <code>IRuntime.validate(IProgressMonitor)</code>.
	 * Clients should never call this method.
	 * </p>
	 *
	 * @return a status object with code <code>IStatus.OK</code> if this
	 * runtime is valid, otherwise a status object indicating what is
	 * wrong with it
	 */
	public IStatus validate() {
		if (runtime.getName() == null || runtime.getName().length() == 0)
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorRuntimeName, null);

		if (isNameInUse())
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorDuplicateRuntimeName, null);
	
		IPath path = runtime.getLocation();
		if (path == null || path.isEmpty())
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, "", null);
		
		return Status.OK_STATUS;
	}

	/**
	 * Returns <code>true</code> if the current name is already in use.
	 * 
	 * @return <code>true</code> if the name is in use, and <code>false</code>
	 *    otherwise
	 */
	private boolean isNameInUse() {
		IRuntime orig = runtime;
		if (runtimeWC != null)
			orig = runtimeWC.getOriginal();
		
		IRuntime[] runtimes = ServerCore.getRuntimes();
		if (runtimes != null) {
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				if (orig != runtimes[i] && runtime.getName().equals(runtimes[i].getName()))
					return true;
			}
		}
		return false;
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
		return runtime.getAttribute(id, defaultValue);
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
		return runtime.getAttribute(id, defaultValue);
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
		return runtime.getAttribute(id, defaultValue);
	}

	/**
	 * Returns the value of the specified List-valued attribute.
	 * 
	 * @param id the attribute id
	 * @param defaultValue the default value of the specified attribute
	 * @return the attribute value
	 * @see #setAttribute(String, List)
	 */
	protected final List getAttribute(String id, List<String> defaultValue) {
		return runtime.getAttribute(id, defaultValue);
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
		return runtime.getAttribute(id, defaultValue);
	}
	
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

	/**
	 * Initializes this runtime with default values. This method is called when
	 * a new runtime is created so that the runtime can be initialized with
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
		runtimeWC.setAttribute(id, value);
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
		runtimeWC.setAttribute(id, value);
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
		runtimeWC.setAttribute(id, value);
	}

	/**
	 * Sets the value of the specified list-valued attribute of this
	 * element. The list may only contain String values.
	 * 
	 * @param id the attribute id
	 * @param value the value of the specified attribute
	 * @see #getAttribute(String, List)
	 */
	protected final void setAttribute(String id, List<String> value) {
		runtimeWC.setAttribute(id, value);
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
		runtimeWC.setAttribute(id, value);
	}
}
