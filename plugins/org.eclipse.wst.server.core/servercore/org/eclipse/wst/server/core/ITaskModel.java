/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
 * This interface represents a task model that can be shared between multiple
 * tasks in a common workflow.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @since 1.0
 */
public interface ITaskModel {
	/**
	 * Task model id for an IRuntime.
	 */
	public static final String TASK_RUNTIME = "runtime";
	
	/**
	 * Task model id for an IServer.
	 */
	public static final String TASK_SERVER = "server";
	
	/**
	 * Task model id for an array of module parents.
	 */
	public static final String TASK_MODULE_PARENTS = "module-parents";
	
	/**
	 * Task model id for an array of modules.
	 */
	public static final String TASK_MODULES = "modules";
	
	/**
	 * Task model id for a launch mode.
	 */
	public static final String TASK_LAUNCH_MODE = "launch-mode";

	/**
	 * Returns the object in the task model with the given id.
	 * 
	 * @param id an id for the object
	 * @return the object with the given id, or <code>null</code>
	 *    if no object could be found with that id
	 */
	public Object getObject(String id);
	
	/**
	 * Put an object into the task model with the given id.
	 * 
	 * @param id the id to associate the object with
	 * @param obj an object, or <code>null</code> to reset the id
	 */
	public void putObject(String id, Object obj);
}