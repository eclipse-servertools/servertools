package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.util.Task;

/**
 * 
 */
public class SaveServerConfigurationTask extends Task {
	public SaveServerConfigurationTask() { }

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		IServerConfigurationWorkingCopy workingCopy = (IServerConfigurationWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER_CONFIGURATION);
		if (workingCopy != null) {
			IFile file = workingCopy.getFile();
			if (file != null && !file.getProject().exists()) {
				IProject project = file.getProject();
				ServerCore.createServerProject(project.getName(), null, monitor);
			}
			workingCopy.save(monitor);
		}
	}
}
