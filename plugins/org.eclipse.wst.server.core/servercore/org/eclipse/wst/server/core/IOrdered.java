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
package org.eclipse.wst.server.core;
/**
 * An object that has an absolute ordering, and can be ordered against other objects.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IOrdered {
	/**
	 * Returns the order (index/priority).
	 * 
	 * @return int
	 */
	public int getOrder();
}