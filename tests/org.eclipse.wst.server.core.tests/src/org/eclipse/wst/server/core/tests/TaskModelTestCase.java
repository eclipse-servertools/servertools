/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests;

import org.eclipse.wst.server.core.TaskModel;

import junit.framework.TestCase;

public class TaskModelTestCase extends TestCase {
	protected static TaskModel taskModel;

	public void testCreate() {
		taskModel = new TaskModel();		
		taskModel.getObject("test");
		taskModel.putObject("test", "test");
	}
}