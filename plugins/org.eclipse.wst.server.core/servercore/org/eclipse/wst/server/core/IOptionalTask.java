/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;
/**
 * An optional task. 
 * 
 * [issue: EY It is not clear to me that when this task will be run. Is this task be used for
 * both the server task and the module task? Will the place where the
 * task is being run depends on the server, e.g. the TDC tasks for the v6 server should be run
 * after the server is started the modules are added to the server; however, the EJB deploy task
 * makes more sense to be run before adding the project to the server.]
 * 
 * @since 1.0
 */
public interface IOptionalTask extends ITask {
    /**
     * Optional task status constant (value 0) indicating that the task
     * should not be run.
     * 
     * @see #getStatus()
     */
	public static final int TASK_UNNECESSARY = 0;
    /**
     * Optional task status constant (value 1) indicating that the task
     * is completed.
     * 
     * @see #getStatus()
     */
	public static final int TASK_COMPLETED = 1;
    /**
     * Optional task status constant (value 2) indicating that the task
     * is ready to be run.
     * 
     * @see #getStatus()
     */
	public static final int TASK_READY = 2;
    /**
     * Optional task status constant (value 3) indicating that the task
     * should be selected to be run by default.
     * 
     * @see #getStatus()
     */
	public static final int TASK_PREFERRED = 3;
    /**
     * Optional task status constant (value 4) indicating that the task
     * must be run.
     * 
     * @see #getStatus()
     */
	public static final int TASK_MANDATORY = 4;

	/**
	 * Returns the status of this task.
     * 
     * issue: EY Should this status can return any of the TASK_XXX? The TASK_XXX looks
     * like a mix of status, e.g. for TASK_COMPLETED and TASK_READY. The other TASK_XXX
     * look like a nature of the task, e.g. TASK_UNNECESSARY, TASK_PREFERRED, TASK_MANDATORY.
     * Is this being used for both status and deciding whether the task should be shown? 
     *  
     * @return one of the status of the task (<code>TASK_XXX</code>)
     * constants declared on {@link IOptionalTask}
	 */
	public int getStatus();
	
	/**
	 * Returns the order (index/priority) of the task that will be run. The task with
     * a smaller order value will be run before the task with a bigger order value.
     * For tasks that have the same order value, the order of running those task are 
     * not guaranteed.  
	 * 
	 * @return the order (index/priority) of the task that will be run.
	 */
	public int getOrder();
}