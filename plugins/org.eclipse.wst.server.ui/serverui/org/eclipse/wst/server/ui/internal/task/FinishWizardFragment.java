package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * 
 */
public class FinishWizardFragment extends WizardFragment {
	protected ITask finishTask;
	
	public FinishWizardFragment(ITask finishTask) {
		this.finishTask = finishTask;
	}
	
	public ITask createFinishTask() {
		return finishTask;
	}
}
