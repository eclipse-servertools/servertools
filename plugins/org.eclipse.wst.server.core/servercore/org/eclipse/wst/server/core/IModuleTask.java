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

import java.util.List;
/**
 * A task for a module.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IModuleTask extends ITask, IOrdered {
	public static final byte TASK_UNNECESSARY = 0;
	public static final byte TASK_COMPLETED = 1;
	public static final byte TASK_READY = 2;
	public static final byte TASK_PREFERRED = 3;
	public static final byte TASK_MANDATORY = 4;

	/**
	 * Returns the id of the adapter.
	 *
	 * @return java.lang.String
	 */
	public String getId();
	
	/**
	 * Return the type ids that may be supported.
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getTypeIds();
	
	/**
	 * Returns true if the given type (given by the id) can use this task. This
	 * result is based on the result of the getTypeIds() method.
	 *
	 * @return boolean
	 */
	public boolean supportsType(String id);

	/**
	 * Lets the task know that it is about to be used. This method should
	 * be used to clean out any previously cached information, or start to
	 * create a new cache.
	 * 
	 * @param server org.eclipse.wst.server.core.IServer
	 * @param configuration org.eclipse.wst.server.core.IServerConfiguration
	 * @param parents java.util.List
	 * @param module org.eclipse.wst.server.core.IModule
	 */
	public void init(IServer server, IServerConfiguration configuration, List parents, IModule module);

	/**
	 * Returns the status of this task.
	 * 
	 * @return byte
	 */
	public byte getTaskStatus();
}