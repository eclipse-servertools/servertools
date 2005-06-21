/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
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
 * The server framework maintains a global list of all known runtime instances
 * ({@link ServerCore#getRuntimes()}).
 * </p>
 * <p>
 * All runtimes have a unique id. Two runtimes (or more likely a runtime and it's
 * working copy) with the same id are equal, and two runtimes with different ids
 * are never equal.
 * </p>
 * <p>
 * Two runtimes are identical if and only if they have the same id.
 * </p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @see IRuntimeWorkingCopy
 * @since 1.0
 */
public interface IRuntime extends IAdaptable {
	/**
	 * Returns the displayable name for this runtime.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name
	 */
	public String getName();

	/**
	 * Returns the id of this runtime instance.
	 * Each runtime (of a given type) has a distinct id, fixed for
	 * its lifetime. Ids are intended to be used internally as keys;
	 * they are not intended to be shown to end users.
	 * <p>
	 * For the id of the runtime type, use {@link IRuntimeType#getId()}
	 * </p>
	 * 
	 * @return the runtime id
	 */
	public String getId();

	/**
	 * Deletes the persistent representation of this runtime.
	 * 
	 * @throws CoreException if there was any error received while deleting the runtime
	 *    or if this method is called on a working copy
	 */
	public void delete() throws CoreException;

	/**
	 * Returns whether this runtime is marked read only.
	 * When a runtime is read only, working copies can be created but
	 * they cannot be saved.
	 *
	 * @return <code>true</code> if this runtime is marked as read only,
	 *    and <code>false</code> otherwise
	 */
	public boolean isReadOnly();

	/**
	 * Returns true if this is a working copy.
	 * 
	 * @return <code>true</code> if this runtime is a working copy
	 *    (can be cast to IRuntimeWorkingCopy), and
	 *    <code>false</code> otherwise
	 */
	public boolean isWorkingCopy();
	
	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> if
	 * no such object can be found, or if the delegate is not
	 * loaded.
	 * <p>
	 * This method will not check the delegate classes for adapting
	 * unless they are already loaded. No plugin loading will occur
	 * when calling this method. It is suitable for popup menus and
	 * other UI artifacts where performance is a concern.
	 * </p>
	 *
	 * @param adapter the adapter class to look up
	 * @return a object castable to the given class, 
	 *    or <code>null</code> if this object does not
	 *    have an adapter for the given class
	 * @see IAdaptable#getAdapter(Class)
	 * @see #loadAdapter(Class, IProgressMonitor)
	 */
	public Object getAdapter(Class adapter);
	
	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> only if
	 * no such object can be found after loading and initializing
	 * delegates.
	 * <p>
	 * This method will force a load and initialization of all delegate
	 * classes and check them for adapting.
	 * </p>
	 *
	 * @param adapter the adapter class to look up
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a object castable to the given class, 
	 *    or <code>null</code> if this object does not
	 *    have an adapter for the given class
	 * @see #getAdapter(Class)
	 */
	public Object loadAdapter(Class adapter, IProgressMonitor monitor);

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
	 * Returns whether this runtime is a stub (used for compilation only)
	 * or a full runtime.
	 * 
	 * @return <code>true</code> if this runtime is a stub, and
	 *    <code>false</code> otherwise
	 */
	public boolean isStub();

	/**
	 * Validates this runtime instance. This method returns an error if the runtime
	 * is pointing to a null or invalid location (e.g. not pointing to the correct
	 * installation directory), or if the runtime-type-specific properties are missing
	 * or invalid.
	 * <p>
	 * This method is not on the working copy so that the runtime can be validated at
	 * any time.
	 * </p>
	 *
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a status object with code <code>IStatus.OK</code> if this
	 *   runtime is valid, otherwise a status object indicating what is
	 *   wrong with it
	 */
	public IStatus validate(IProgressMonitor monitor);
}