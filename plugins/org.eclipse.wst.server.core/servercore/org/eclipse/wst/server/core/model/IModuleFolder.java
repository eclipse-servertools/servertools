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
/**
 * A folder within a module.
 * 
 * @since 1.0
 */
public interface IModuleFolder extends IModuleResource {
	/**
	 * Returns the members (contents) of this folder.
	 * 
	 * @return an array containing the module resources contained in this folder
	 */
	public IModuleResource[] members();
}