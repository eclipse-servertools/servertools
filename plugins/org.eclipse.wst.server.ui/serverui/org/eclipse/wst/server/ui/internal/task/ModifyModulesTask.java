package org.eclipse.wst.server.ui.internal.task;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.util.Task;

/**
 * 
 */
public class ModifyModulesTask extends Task {
	protected List add;
	protected List remove;
	
	public ModifyModulesTask() { }
	
	public void setAddModules(List add) {
		this.add = add;
	}
	
	public void setRemoveModules(List remove) {
		this.remove = remove;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		if ((add == null || add.isEmpty()) && (remove == null || remove.isEmpty()))
			return;

		IServerWorkingCopy workingCopy = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);

		// modify modules
		IModule[] remove2 = new IModule[0];
		if (remove != null) {
			remove2 = new IModule[remove.size()];
			remove.toArray(remove2);
		}
		
		IModule[] add2 = new IModule[0];
		if (add != null) {
			add2 = new IModule[add.size()];
			add.toArray(add2);
		}
		
		workingCopy.modifyModules(add2, remove2, monitor);
	}
}