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
/**
 * A file within a module.
 * 
 * @since 1.0
 */
public interface IModuleFile extends IModuleResource {
	/**
	 * Returns a modification stamp. Whenever the modification
	 * stamp changes, there may have been a change to the file.
	 * 
	 * @return the modification stamp
	 */
	public long getModificationStamp();
}