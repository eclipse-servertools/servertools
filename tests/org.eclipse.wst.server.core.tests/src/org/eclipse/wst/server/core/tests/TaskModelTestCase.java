/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests;

import org.eclipse.wst.server.core.TaskModel;

import junit.framework.TestCase;

public class TaskModelTestCase extends TestCase {
	protected static TaskModel taskModel;

	public void testListener() {
		taskModel = new TaskModel();		
		taskModel.getObject("test");
		taskModel.putObject("test", "test");
	}
}