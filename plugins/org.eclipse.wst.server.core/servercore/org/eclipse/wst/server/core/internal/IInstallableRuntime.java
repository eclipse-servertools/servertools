/*******************************************************************************
 * Copyright (c) 2005, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * Represents an installable runtime.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IInstallableRuntime {
	/**
	 * Returns the id of this runtime type.
	 * Each known server runtime type has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the runtime type id
	 */
	public String getId();

	/**
	 * Provide the top-level path in the archive
	 * @return
	 */
	public String getArchivePath();

	/**
	 * Provides a name for this runtime.
	 * 
	 * @return a name
	 */
	public String getName();


	/**
	 * Downloads the license from the update site.
	 * 
	 * @param monitor a progress monitor
	 * @return the license
	 * @throws CoreException
	 */
	public String getLicense(IProgressMonitor monitor) throws CoreException;

	/**
	 * Kicks off a background job to install the runtime.
	 * 
	 * @param path the path to install the runtime at
	 */
	public void install(IPath path);

	/**
	 * Install this runtime.
	 * 
	 * @param path the path to install the runtime at
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired 
	 * @throws CoreException if an exception occurs while creating this runtime
	 *    or setting it's default values
	 */
	public void install(IPath path, IProgressMonitor monitor) throws CoreException;
}