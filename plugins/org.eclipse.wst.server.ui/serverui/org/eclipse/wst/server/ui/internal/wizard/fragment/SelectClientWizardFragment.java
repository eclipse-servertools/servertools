/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.ui.internal.wizard.page.SelectClientComposite;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * A fragment used to select a client.
 */
public class SelectClientWizardFragment extends WizardFragment {
	protected IClient[] clients;
	protected SelectClientComposite comp;

	public SelectClientWizardFragment(IClient[] clients) {
		super();
		this.clients = clients;
	}

	public boolean hasComposite() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new SelectClientComposite(parent, wizard, clients);
		return comp;
	}

	/**
	 * Return the selected client.
	 * 
	 * @return the client
	 */
	public IClient getSelectedClient() {
		return comp.getSelectedClient();
	}
}