/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import java.beans.PropertyChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * A working copy runtime object used for formulating changes
 * to a runtime instance ({@link IRuntime}). Changes made on a
 * working copy do not occur (and are not persisted) until a
 * save() is performed. 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @see IRuntime
 * @since 1.0
 */
public interface IRuntimeWorkingCopy extends IRuntime {
	public static final int TIMESTAMP_ERROR = 5;

	/**
	 * Sets the displayable name for this runtime.
	 * <p>
	 * The name should be appropriate for the current locale.
	 * </p>
	 *
	 * @param name a displayable name
	 * @see IRuntime#getName()
	 */
	public void setName(String name);
	
	/**
	 * Sets or unsets whether this runtime is marked as read only.
	 * When a runtime is read only, working copies can be created but
	 * they cannot be saved.
	 *
	 * @param readOnly <code>true</code> to set this runtime to be marked
	 *    read only, and <code>false</code> to unset
	 */
	public void setReadOnly(boolean readOnly);
	
	/**
	 * Sets whether this runtime is private.
	 * Generally speaking, runtimes marked private are internal ones
	 * that should not be shown to users (because they won't know
	 * anything about them).
	 * 
	 * @param p <code>true</code> if this runtime is private,
	 *    and <code>false</code> otherwise
	 * @see IRuntime#isPrivate()
	 */
	public void setPrivate(boolean p);
	
	/**
	 * Returns whether this working copy has unsaved changes.
	 * 
	 * @return <code>true</code> if this working copy has unsaved
	 *    changes, and <code>false</code> otherwise
	 */
	public boolean isDirty();

	/**
	 * Adds a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Removes a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Fires a property change event.
	 */
	public void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue);

	/**
	 * Returns the runtime instance that this working copy is
	 * associated with.
	 * <p>
	 * For a runtime working copy created by a call to
	 * {@link IRuntime#createWorkingCopy()},
	 * <code>this.getOriginal()</code> returns the original
	 * runtime object. For a runtime working copy just created by
	 * a call to {@link IRuntimeType#createRuntime(String)},
	 * <code>this.getOriginal()</code> returns <code>null</code>.
	 * </p>
	 * 
	 * @return the associated runtime instance, or <code>null</code> if none
	 */
	public IRuntime getOriginal();
	
	/**
	 * Returns the extension for this runtime working copy.
	 * The runtime working copy extension is a
	 * runtime-type-specific object. By casting the runtime working copy
	 * extension to the type prescribed in the API documentation for that
	 * particular runtime working copy type, the client can access
	 * runtime-type-specific properties and methods.
	 * 
	 * @return the extension for the runtime working copy
	 */
	//public IServerExtension getWorkingCopyExtension(IProgressMonitor monitor);

	/**
	 * Sets the absolute path in the local file system to the root of the runtime,
	 * typically the installation directory. 
	 * 
	 * @param path the location of this runtime, or <code>null</code> if none
	 * @see IRuntime#getLocation()
	 */
	public void setLocation(IPath path);

	/**
	 * Commits the changes made in this working copy. If there is
	 * no extant runtime instance with a matching id and runtime
	 * type, this will create a runtime instance with attributes
	 * taken from this working copy, and return that object.
	 * <p>
	 * If there an existing runtime instance with a matching id and
	 * runtime type, this will change the runtime instance accordingly.
	 * The returned runtime will be the same runtime this is returned
	 * from getOriginal(), after the changes have been applied.
	 * Otherwise, this method will return a newly created runtime.
	 * </p>
	 * Runtimes can be saved even when they have invalid properties. It
	 * is the clients responsibility to call validate() or check the
	 * properties before saving.
	 * <p>
	 * [issue: What is lifecycle for RuntimeWorkingCopyDelegate
	 * associated with this working copy?]
	 * </p>
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new runtime instance
	 * @throws CoreException if the save could not be completed
	 */
	public IRuntime save(boolean force, IProgressMonitor monitor) throws CoreException;
}