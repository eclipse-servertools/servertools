/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
/**
 * 
 * 
 * @since 1.0
 */
class TaskWizardPage extends WizardPage implements IWizardHandle {
	protected WizardFragment fragment;
	
	protected boolean isEmptyError = false;

	public TaskWizardPage(WizardFragment fragment) {
		super(fragment.toString());
		this.fragment = fragment;
	}
	
	public void createControl(Composite parentComp) {
		Composite comp = fragment.createComposite(parentComp, this);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = convertHorizontalDLUsToPixels(150);
		//data.heightHint = convertVerticalDLUsToPixels(350);
		comp.setLayoutData(data);
		setControl(comp);
	}

	public boolean isPageComplete() {
		if (!fragment.isComplete())
			return false;
		if (isEmptyError)
			return false;
		return (getMessage() == null || getMessageType() != ERROR);
	}

	public boolean canFlipToNextPage() {
		if (getNextPage() == null)
			return false;
		if (isEmptyError)
			return false;
		return (getMessage() == null || getMessageType() != ERROR);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if (visible) {
			TaskWizard wizard = (TaskWizard) getWizard();
			wizard.switchWizardFragment(fragment);
			
			if (getContainer().getCurrentPage() != null)
				getContainer().updateButtons();
		}
	}
	
	public void setMessage(String message, int type) {
		if (type == IMessageProvider.ERROR && "".equals(message)) {
			isEmptyError = true;
			message = null;
		} else
			isEmptyError = false;
		super.setMessage(message, type);
		WizardFragment frag = ((TaskWizard) getWizard()).getCurrentWizardFragment();
		if (!fragment.equals(frag))
			return;
		getContainer().updateButtons();
	}
	
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InterruptedException, InvocationTargetException {
		getWizard().getContainer().run(fork, cancelable, runnable);
	}

	public void update() {
		fragment.updateChildFragments();
		((TaskWizard) getWizard()).updatePages();
		if (getContainer().getCurrentPage() != null)
			getContainer().updateButtons();
	}
}