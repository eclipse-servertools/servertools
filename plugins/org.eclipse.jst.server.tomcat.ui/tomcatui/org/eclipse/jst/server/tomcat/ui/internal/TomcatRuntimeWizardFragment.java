package org.eclipse.jst.server.tomcat.ui.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class TomcatRuntimeWizardFragment extends WizardFragment {
	protected TomcatRuntimeComposite comp;
	
	public TomcatRuntimeWizardFragment() { }

	public boolean hasComposite() {
		return true;
	}
	
	public boolean isComplete() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_RUNTIME);
		
		if (runtime == null)
			return false;
		IStatus status = runtime.validate(null);
		return (status != null && status.isOK());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.task.WizardFragment#createComposite()
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new TomcatRuntimeComposite(parent, wizard);
		return comp;
	}

	public void enter() {
		if (comp != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_RUNTIME);
			comp.setRuntime(runtime);
		}
	}

	public void exit() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_RUNTIME);
		IPath path = runtime.getLocation();
		if (runtime.validate(null).isOK())
			TomcatPlugin.setPreference("location" + runtime.getRuntimeType().getId(), path.toString());
	}
}