/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.IPath;
/**
 * A resource (file or folder) within a module.
 */
public interface IModuleResource {
	/**
	 * Returns the module relative path to this resource.
	 * 
	 * @return
	 */
	public IPath getModuleRelativePath();
	
	/**
	 * Returns the name of this resource.
	 * 
	 * @return
	 */
	public String getName();
}