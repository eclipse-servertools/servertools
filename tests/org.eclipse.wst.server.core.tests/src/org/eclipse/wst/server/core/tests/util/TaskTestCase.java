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
package org.eclipse.wst.server.core.tests.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.util.Task;
import junit.framework.Test;
import junit.framework.TestCase;

public class TaskTestCase extends TestCase {
	protected static Task task;
	
	public static Test suite() {
		return new OrderedTestSuite(TaskTestCase.class, "TaskTestCase");
	}

	public void test00Create() {
		task = new Task() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				// do nothing
			}
		};
	}
	
	public void test01Create() {
		task = new Task("name", "description") {
			public void execute(IProgressMonitor monitor) throws CoreException {
				// do nothing
			}
		};
	}
	
	public void test02GetName() {
		assertNotNull(task.getName());
	}
	
	public void test03GetDescription() {
		assertNotNull(task.getDescription());
	}
	
	public void test04GetTaskModel() {
		assertNull(task.getTaskModel());
	}
	
	public void test05SetTaskModel() {
		task.setTaskModel(null);
	}
	
	public void test06Execute() throws Exception {
		task.execute(null);
	}
	
	public void test07CanUndo() {
		task.canUndo();
	}
	
	public void test08Undo() {
		task.undo();
	}
}