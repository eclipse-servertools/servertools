package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.util.Task;

/**
 * 
 */
public class ReleaseServerTask extends Task {
	public ReleaseServerTask() { }

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		IServerWorkingCopy workingCopy = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);
		if (workingCopy != null)
			workingCopy.release();
	}
}
