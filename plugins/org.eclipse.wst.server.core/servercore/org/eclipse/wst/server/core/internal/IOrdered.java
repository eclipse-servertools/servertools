/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;
/**
 * An object that has an absolute ordering, and can be ordered against other objects.
 * 
 * <p>
 * [issue: It is notoriously difficult to place any kind of
 * useful order on objects that are contributed independently by
 * non-collaborating parties. The IOrdered mechanism is weak, and
 * can't really solve the problem. Issues of presentation are usually
 * best left to the UI, which can sort objects based on arbitrary
 * properties.]
 * </p>
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