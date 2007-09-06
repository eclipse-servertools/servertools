/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.ui.internal.wizard.page.TasksComposite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class SubTasksWizardFragment extends WizardFragment {
	protected TasksComposite comp;

	protected List tasks;

	public SubTasksWizardFragment() {
		// do nothing
	}

	public void enter() {
		updateTasks(tasks);
		
		if (comp != null)
			comp.createControl();
	}

	public void updateTasks(List newTasks) {
		tasks = newTasks;
		if (comp != null)
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (comp != null && !comp.isDisposed())
						comp.setTasks(tasks);
				}
			});
	}

	public boolean hasComposite() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new TasksComposite(parent, wizard);
		return comp;
	}
}
