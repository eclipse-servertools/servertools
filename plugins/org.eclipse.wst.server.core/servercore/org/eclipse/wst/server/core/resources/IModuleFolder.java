/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
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
 * A remote folder is a remote resource that can have children.
 */
public interface IModuleFolder extends IModuleResource {
	/**
	 * Returns a list of IModuleResources directly contained
	 * within this folder.
	 *
	 * @return java.util.List
	 */
	public IModuleResource[] members() throws CoreException;
}