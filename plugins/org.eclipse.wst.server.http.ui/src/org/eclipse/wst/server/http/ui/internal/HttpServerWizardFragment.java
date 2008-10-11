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
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * HTTP server wizard fragment.
 */
public class HttpServerWizardFragment extends WizardFragment {
	protected HttpServerComposite comp;

	public boolean hasComposite() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.ui.task.WizardFragment#createComposite()
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new HttpServerComposite(parent, wizard);
		return comp;
	}

	/*
	 * @see org.eclipse.wst.server.ui.wizard.WizardFragment#enter()
	 */
	public void enter() {
		if (comp != null) {
			IServerWorkingCopy runtime = (IServerWorkingCopy) getTaskModel().getObject(TaskModel.TASK_SERVER);
			comp.setServer(runtime);
		}
	}
	
	public boolean isComplete() {
		if (comp != null)
			return comp.isComplete();
		return true;
	}
}