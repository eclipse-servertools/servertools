package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.util.Task;

/**
 * 
 */
public class SaveRuntimeTask extends Task {
	public SaveRuntimeTask() { }

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		IRuntime runtime = (IRuntime) getTaskModel().getObject(ITaskModel.TASK_RUNTIME);
		if (runtime instanceof IRuntimeWorkingCopy) {
			IRuntimeWorkingCopy workingCopy = (IRuntimeWorkingCopy) runtime; 
			workingCopy.save(monitor);
		}
	}
}