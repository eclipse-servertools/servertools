package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.util.Task;
/**
 * 
 */
public class InputTask extends Task {
	protected String[] ids;
	protected Object[] values;
	
	public InputTask(String id, Object value) {
		this(new String[] { id }, new Object[] { value });
	}

	public InputTask(String[] ids, Object[] values) {
		this.ids = ids;
		this.values = values;
	}
	
	public void execute(IProgressMonitor monitor) {
		int size = ids.length;
		for (int i = 0; i < size; i++) {
			getTaskModel().putObject(ids[i], values[i]);
		}
	}
}
