/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;
/**
 * An optional task.
 */
public interface IOptionalTask extends ITask {
	public static final int TASK_UNNECESSARY = 0;
	public static final int TASK_COMPLETED = 1;
	public static final int TASK_READY = 2;
	public static final int TASK_PREFERRED = 3;
	public static final int TASK_MANDATORY = 4;

	/**
	 * Returns the status of this task.
	 * 
	 * @return byte
	 */
	public int getStatus();
	
	/**
	 * Returns the order (index/priority).
	 * 
	 * @return int
	 */
	public int getOrder();
}