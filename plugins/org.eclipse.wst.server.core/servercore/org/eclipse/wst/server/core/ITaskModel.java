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
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface ITaskModel {
	public static final String TASK_RUNTIME = "runtime";
	public static final String TASK_SERVER = "server";
	public static final String TASK_SERVER_CONFIGURATION = "server-configuration";
	
	public static final String TASK_MODULE_PARENTS = "module-parents";
	public static final String TASK_MODULES = "modules";
	
	public static final String TASK_LAUNCH_MODE = "launch-mode";

	public Object getObject(String id);
	
	public void putObject(String id, Object obj);
}