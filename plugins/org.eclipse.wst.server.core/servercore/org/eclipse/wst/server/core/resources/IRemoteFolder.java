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

import java.util.List;
/**
 * A remote folder is a remote resource that can have children.
 */
public interface IRemoteFolder extends IRemoteResource {
	/**
	 * Returns a list of IRemoteResources directly contained
	 * within this folder.
	 *
	 * @return java.util.List
	 */
	public List getContents();
}
