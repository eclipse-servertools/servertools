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
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @see IRuntime
 * @plannedfor 1.0
 */
public interface IRuntimeWorkingCopy extends IRuntime {
	/**
	 * Status code (value 1) returned from the save() method when the save
	 * failed with force set to <code>false</code> because the runtime has
	 * been modified and saved since this working copy was created.
	 * 
	 * @see #save(boolean, IProgressMonitor)
	 */
	public static final int SAVE_CONFLICT = 1;
	
	/**
	 * Property change name (value "name") used when the name of the runtime
	 * changes.
	 * 
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 * @see #removePropertyChangeListener(PropertyChangeListener)
	 */
	public static final String PROPERTY_NAME = "name";
	
	/**
	 * Property change name (value "location") used when the location of the
	 * runtime changes.
	 * 
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 * @see #removePropertyChangeListener(PropertyChangeListener)
	 */
	public static final String PROPERTY_LOCATION = "location";

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
	 * Returns whether this working copy has unsaved changes.
	 * 
	 * @return <code>true</code> if this working copy has unsaved
	 *    changes, and <code>false</code> otherwise
	 */
	public boolean isDirty();

	/**
	 * Adds a property change listener to this runtime.
	 * <p>
	 * Once registered, a listener starts receiving notification of 
	 * property changes to this runtime. The listener continues to receive
	 * notifications until it is removed.
	 * Has no effect if an identical listener is already registered.
	 * </p>
	 *
	 * @param listener a property change listener
	 * @see #removePropertyChangeListener(PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Removes a property change listener from this runtime.
	 * Has no effect if the listener is not registered.
	 *
	 * @param listener a property change listener
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Returns the runtime instance that this working copy is
	 * associated with.
	 * <p>
	 * For a runtime working copy created by a call to
	 * {@link IRuntime#createWorkingCopy()},
	 * <code>this.getOriginal()</code> returns the original
	 * runtime object. For a runtime working copy just created by
	 * a call to {@link IRuntimeType#createRuntime(String, IProgressMonitor)},
	 * <code>this.getOriginal()</code> returns <code>null</code>.
	 * </p>
	 * 
	 * @return the associated runtime instance, or <code>null</code> if none
	 */
	public IRuntime getOriginal();

	/**
	 * Sets the absolute path in the local file system to the root of the runtime,
	 * typically the installation directory. 
	 * 
	 * @param path the location of this runtime, or <code>null</code> if none
	 * @see IRuntime#getLocation()
	 */
	public void setLocation(IPath path);

	/**
	 * Returns whether this runtime is a stub (used for compilation only)
	 * or a full runtime.
	 * 
	 * @param stub <code>true</code> if this runtime is a stub, and
	 *    <code>false</code> otherwise
	 */
	public void setStub(boolean stub);

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
	 * @param force <code>true</code> to force the save, or <code>false</code>
	 *    otherwise
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new runtime instance
	 * @throws CoreException if the save could not be completed
	 * @see #SAVE_CONFLICT
	 */
	public IRuntime save(boolean force, IProgressMonitor monitor) throws CoreException;
}