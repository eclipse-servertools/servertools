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
package org.eclipse.wst.server.core.resources;

import org.eclipse.core.runtime.CoreException;
/**
 * A module folder is leaf or non-leaf resource in a module.
 * Folders may contain files and/or other folders. All members
 * belong to the same module as their parent.
 * <p>
 * This interface is not intended to be implemented by clients
 * other than module factories.
 * </p>
 * <p>
 * [issue: See issues on IModuleResource about how to get rid
 * of this interface.]
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IModuleFolder extends IModuleResource {

	/**
	 * Returns a list of existing member resources (folders and files)
	 * in this folder, in no particular order. 
	 * <p>
	 * Note that the members of a folder are the files and folders
	 * immediately contained within it. All members belong to the
	 * same module as this folder.
	 * </p>
	 *
	 * @return a list of members of this folder
	 * @exception CoreException if this method fails
	 */
	public IModuleResource[] members() throws CoreException;
}