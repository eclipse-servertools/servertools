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
package org.eclipse.wst.server.core.model;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
/**
 * A module task.
 * 
 * <p>This is the implementation of a moduleTask extension point.</p>
 */
public interface IModuleTaskDelegate {
	public static final byte TASK_UNNECESSARY = 0;
	public static final byte TASK_COMPLETED = 1;
	public static final byte TASK_READY = 2;
	public static final byte TASK_PREFERRED = 3;
	public static final byte TASK_MANDATORY = 4;
	
	/**
	 * Lets the task know that it is about to be used. This method should
	 * be used to clean out any previously cached information, or start to
	 * create a new cache.
	 * 
	 * @param server org.eclipse.wst.server.core.model.IServer
	 * @param configuration org.eclipse.wst.server.core.model.IServerConfiguration
	 * @param parents java.util.List
	 * @param module org.eclipse.wst.server.core.model.IModule
	 */
	public void init(IServer server, IServerConfiguration configuration, List parents, IModule module);

	/**
	 * Returns the status of this task.
	 * 
	 * @return byte
	 */
	public byte getTaskStatus();

	/**
	 * Perform the task.
	 *
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public void execute(IProgressMonitor monitor) throws CoreException;
}
