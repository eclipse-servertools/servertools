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

import java.util.HashMap;
import java.util.Map;
/**
 * A task model represents a model that can be shared between multiple
 * tasks in a common workflow.
 * <p>
 * The task model contains information about the overall task flow and allows
 * tasks to store and retreive data. Its usage allows mutliple tasks to be
 * chained together and share data from the output of one task to the input
 * of another.
 * </p>
 * 
 * @plannedfor 1.0
 */
public class TaskModel {
	/**
	 * Task model id for an IRuntime.
	 * 
	 * @see #getObject(String)
	 * @see #putObject(String, Object)
	 */
	public static final String TASK_RUNTIME = "runtime";
	
	/**
	 * Task model id for an IServer.
	 * 
	 * @see #getObject(String)
	 * @see #putObject(String, Object)
	 */
	public static final String TASK_SERVER = "server";

	/**
	 * Task model id for an array of modules.
	 * The value is a List containing IModule[] arrays.
	 * 
	 * @see #getObject(String)
	 * @see TaskModel#putObject(String, Object)
	 */
	public static final String TASK_MODULES = "modules";
	
	/**
	 * Task model id for a launch mode.
	 * 
	 * @see #getObject(String)
	 * @see #putObject(String, Object)
	 */
	public static final String TASK_LAUNCH_MODE = "launch-mode";
	
	private Map map = new HashMap();

	/**
	 * Returns the object in the task model with the given id.
	 * <p>
	 * The id can be any of the predefined ids within TaskModel, or
	 * any other key to retreive task-specific data.
	 * </p>
	 * 
	 * @param id an id for the object
	 * @return the object with the given id, or <code>null</code>
	 *    if no object could be found with that id
	 */
	public Object getObject(String id) {
		try {
			return map.get(id);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Put an object into the task model with the given id.
	 * <p>
	 * The id can be any of the predefined ids within TaskModel, or
	 * any other key to store task-specific data. 
	 * </p>
	 * 
	 * @param id the id to associate the object with
	 * @param obj an object, or <code>null</code> to reset (clear) the id
	 */
	public void putObject(String id, Object obj) {
		map.put(id, obj);
	}
}