/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.viewers;
/**
 * Extends <code>ITableLabelProvider</code> with methods to provide
 * lock information.
 */
public interface ILockedLabelProvider {
	/**
	 * Returns the lock info for the element. This value will be used
	 * to change the presentation of the table row.
	 *
	 * @param element the object representing the entire row, or 
	 *    <code>null</code> indicating that no input object is set
	 *    in the viewer
	 * @return <code>true</code> if the item is locked, and <code>false</code>
	 *    otherwise
	 */
	public boolean isLocked(Object element);
}