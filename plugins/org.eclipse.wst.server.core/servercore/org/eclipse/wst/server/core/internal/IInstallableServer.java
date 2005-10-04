/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * Represents an installable server adapter.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IInstallableServer {
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
	 * Returns the displayable name for this runtime type.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this runtime type
	 */
	public String getName();

	/**
	 * Returns the displayable description for this runtime type.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable description for this runtime type
	 */
	public String getDescription();

	/**
	 * Returns the displayable vendor name for this runtime type. If the
	 * runtime type did not specific a vendor, an empty string is returned.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable vendor name for this runtime type
	 */
	public String getVendor();

	/**
	 * Returns the displayable version name for this runtime type. If the
	 * runtime type did not specific a vendor, an empty string is returned.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable version name for this runtime type
	 */
	public String getVersion();

	/**
	 * Install this server.
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired 
	 * @throws CoreException if an exception occurs while creating this runtime
	 *    or setting it's default values
	 */
	public void install(IProgressMonitor monitor) throws CoreException;
}