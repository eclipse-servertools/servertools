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
package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.util.Task;
/**
 * 
 */
public class SaveRuntimeTask extends Task {
	public SaveRuntimeTask() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		IRuntime runtime = (IRuntime) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
		if (runtime != null && runtime instanceof IRuntimeWorkingCopy) {
			IRuntimeWorkingCopy workingCopy = (IRuntimeWorkingCopy) runtime;
			if (workingCopy.isDirty())
				getTaskModel().putObject(TaskModel.TASK_RUNTIME, workingCopy.save(false, monitor));
		}
	}
}