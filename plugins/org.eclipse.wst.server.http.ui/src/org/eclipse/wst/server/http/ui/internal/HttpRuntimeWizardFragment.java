/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.http.ui.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * HTTP runtime wizard fragment.
 */
public class HttpRuntimeWizardFragment extends WizardFragment {
	protected HttpRuntimeComposite comp;

	/**
	 * ServerWizardFragment constructor
	 */
	public HttpRuntimeWizardFragment() {
		// do nothing
	}

	public boolean hasComposite() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.ui.task.WizardFragment#createComposite()
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new HttpRuntimeComposite(parent, wizard);
		return comp;
	}

	/*
	 * @see org.eclipse.wst.server.ui.wizard.WizardFragment#enter()
	 */
	public void enter() {
		if (comp != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
			comp.setRuntime(runtime);
		}
	}

	public boolean isComplete() {
		if (comp != null)
			return comp.isComplete();
		return true;
	}
}