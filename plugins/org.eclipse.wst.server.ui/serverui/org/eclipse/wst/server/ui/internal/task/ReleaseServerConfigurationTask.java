package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.util.Task;

/**
 * 
 */
public class ReleaseServerConfigurationTask extends Task {
	public ReleaseServerConfigurationTask() { }

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		IServerConfigurationWorkingCopy workingCopy = (IServerConfigurationWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER_CONFIGURATION);
		if (workingCopy != null)
			workingCopy.release();
	}
}