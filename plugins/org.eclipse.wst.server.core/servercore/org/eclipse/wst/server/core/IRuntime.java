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
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.*;
/**
 * Represents a runtime instance. Every runtime is an instance of a
 * particular, fixed runtime type.
 * <p>
 * Servers have a runtime. The server runtime corresponds to the
 * installed code base for the server. The main role played by the server
 * runtime is in identifying code libraries to compile or build against.
 * In the case of local servers, the server runtime may play a secondary role
 * of being used to launch the server for testing. Having the server runtimes
 * identified as an entity separate from the server itself facilitates sharing
 * server runtimes between several servers.
 * </p>
 * <p>
 * IRuntime implements IAdaptable to allow users to obtain a runtime-type-specific
 * class. By casting the runtime extension to the type prescribed in the API
 * documentation for that particular runtime type, the client can access
 * runtime-type-specific properties and methods. getAdapter() may involve plugin
 * loading, and should not be called from popup menus, etc.
 * </p>
 * <p>
 * [issue: As mentioned in an issue on IRuntimeType, the term "runtime"
 * is misleading, given that the main reason is for build time classpath
 * contributions, not for actually running anything. "libraries" might be a
 * better choice.]
 * </p>
 * <p>
 * The server framework maintains a global list of all known runtime instances
 * ({@link ServerCore#getRuntimes()}).
 * </p>
 * <p>
 * All runtimes have a unique id. Two runtimes (or more likely a runtime and it's
 * working copy) with the same id are equal, and two runtimes with different ids
 * are never equal.
 * </p>
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IRuntime extends IElement, IAdaptable {
	/**
	 * Returns the type of this runtime instance.
	 * 
	 * @return the runtime type
	 */
	public IRuntimeType getRuntimeType();

	/**
	 * Returns a runtime working copy for modifying this runtime instance.
	 * If this instance is already a working copy, it is returned.
	 * If this instance is not a working copy, a new runtime working copy
	 * is created with the same id and attributes.
	 * Clients are responsible for saving or releasing the working copy when
	 * they are done with it.
	 * <p>
	 * The runtime working copy is related to this runtime instance
	 * in the following ways:
	 * <pre>
	 * this.getWorkingCopy().getId() == this.getId()
	 * this.getWorkingCopy().getOriginal() == this
	 * </pre>
	 * </p>
	 * <p>
	 * [issue: IRuntimeWorkingCopy extends IRuntime. 
	 * Runtime.getWorkingCopy() create a new working copy;
	 * RuntimeWorkingCopy.getWorkingCopy() returns this.
	 * This may be convenient in code that is ignorant of
	 * whether they are dealing with a working copy or not.
	 * However, it is hard for clients to manage working copies
	 * with this design.
	 * </p>
	 * 
	 * @return a new working copy
	 */
	public IRuntimeWorkingCopy createWorkingCopy();

	/**
	 * Returns the absolute path in the local file system to the root of the runtime,
	 * typically the installation directory.
	 * 
	 * @return the location of this runtime, or <code>null</code> if none
	 */
	public IPath getLocation();

	/**
	 * Returns whether this runtime is a stub (used for compilation only) or a full runtime.
	 * 
	 * @return <code>true</code> if this runtime is a stub, and <code>false</code> otherwise
	 */
	public boolean isStub();

	/**
	 * Validates this runtime instance. This method should return an error if the runtime
	 * is pointing to a null or invalid location (e.g. not pointing to the correct installation
	 * directory), or if the runtime-type-specific properties are missing or invalid.
	 * <p>
	 * This method is not on the working copy so that the runtime can be validated at any time.
	 * </p>
	 *
	 * @return a status object with code <code>IStatus.OK</code> if this
	 * runtime is valid, otherwise a status object indicating what is
	 * wrong with it
	 */
	public IStatus validate(IProgressMonitor monitor);
}