package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class InputWizardFragment extends WizardFragment {
	protected String[] ids;
	protected Object[] values;
	
	public InputWizardFragment(String id, Object value) {
		this(new String[] { id }, new Object[] { value });
	}

	public InputWizardFragment(String[] ids, Object[] values) {
		this.ids = ids;
		this.values = values;
	}
	
	public void enter() {
		int size = ids.length;
		for (int i = 0; i < size; i++) {
			getTaskModel().putObject(ids[i], values[i]);
		}
	}
}