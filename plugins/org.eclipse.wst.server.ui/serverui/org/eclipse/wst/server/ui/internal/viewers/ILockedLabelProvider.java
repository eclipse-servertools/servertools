/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;
/**
 * Extends <code>ITableLabelProvider</code> with methods to provide
 * lock information.
 *
 * @see TableViewer
 */
public interface ILockedLabelProvider {
	/**
	 * Returns the lock info for the element. This value will be used
	 * to change the presentation of the table row.
	 *
	 * @param element the object representing the entire row, or 
	 *    <code>null</code> indicating that no input object is set
	 *    in the viewer
	 */
	public boolean isLocked(Object element);
}