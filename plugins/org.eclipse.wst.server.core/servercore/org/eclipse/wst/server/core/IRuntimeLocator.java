/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * A runtime locator provides the ability to locate or search for additional
 * runtimes of a particular type.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @since 1.0
 */
public interface IRuntimeLocator {
	public interface RuntimeSearchListener {
		/**
		 * Called when a new runtime is found by the locator.
		 * The runtime must never be null.
		 * 
		 * @param runtime the runtime that was found.
		 */
		public void runtimeFound(IRuntimeWorkingCopy runtime);
	}

	/**
	 * Returns the id of this runtime locator.
	 * Each known runtime locator has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the runtime locator id
	 */
	public String getId();

	/**
	 * Returns the displayable name for this runtime locator.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this runtime locator
	 */
	public String getName();

	/**
	 * Returns the displayable description for this runtime locator.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable description for this runtime locator
	 */
	public String getDescription();

	/**
	 * Returns true if the runtime locator can find runtimes of the given type.
	 * The id should never be null.
	 * 
	 * @param runtimeTypeId the id of a runtime type
	 * @return boolean
	 */
	public boolean supportsType(String runtimeTypeId);

	/**
	 * Searches for local runtimes. 
	 * It uses the callback listener to report runtimes that are found.
	 * The path contains the absolute path of the folder to search in,
	 * or <code>null</code> to search the entire machine.
	 * 
	 * @param path the path to search for runtimes in
	 * @param listener a listener to report status to
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException
	 */
	public void searchForRuntimes(IPath path, RuntimeSearchListener listener, IProgressMonitor monitor) throws CoreException;
}