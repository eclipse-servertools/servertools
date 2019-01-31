/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
/**
 * A resource (file or folder) within a module.
 * 
 * @since 1.0
 */
public interface IModuleResource extends IAdaptable {
	/**
	 * Returns the module relative path to this resource.
	 * 
	 * @return the module relative path to this resource
	 */
	public IPath getModuleRelativePath();

	/**
	 * Returns the name of this resource.
	 * 
	 * @return the name of this resource
	 */
	public String getName();
}