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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IOptionalTask;
import org.eclipse.wst.server.core.TaskModel;

import junit.framework.TestCase;

public class IOptionalTaskTestCase extends TestCase {
	protected static IOptionalTask task;

	public void testCreate() throws Exception {
		task = new IOptionalTask() {
			public String getName() {
				return null;
			}

			public String getDescription() {
				return null;
			}

			public TaskModel getTaskModel() {
				return null;
			}

			public void setTaskModel(TaskModel taskModel) {
				// ignore
			}

			public boolean canExecute() {
				return false;
			}

			public void execute(IProgressMonitor monitor) throws CoreException {
				// ignore
			}

			public boolean canUndo() {
				return false;
			}

			public void undo() {
				// ignore
			}

			public int getStatus() {
				return 0;
			}

			public int getOrder() {
				return 0;
			}
		};
		task.getName();
		task.getDescription();
		task.getTaskModel();
		task.setTaskModel(null);
		task.canExecute();
		task.execute(null);
		task.canUndo();
		task.undo();
		task.getStatus();
		task.getOrder();
	}
}