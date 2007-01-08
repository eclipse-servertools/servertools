/**********************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.wizard;

import java.util.List;

import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.wizard.fragment.SelectClientWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A wizard used to select a client from a list.
 */
public class SelectClientWizard extends TaskWizard {
	protected static SelectClientWizardFragment fragment;

	/**
	 * SelectClientWizard constructor comment.
	 * 
	 * @param clients an array of clients
	 */
	public SelectClientWizard(final IClient[] clients) {
		super(Messages.wizSelectClientWizardTitle, new WizardFragment() {
			protected void createChildFragments(List list) {
				fragment = new SelectClientWizardFragment(clients);
				list.add(fragment);
			}
		});
		
		setForcePreviousAndNextButtons(true);
	}

	/**
	 * Return the selected client.
	 * @return the client
	 */
	public IClient getSelectedClient() {
		return fragment.getSelectedClient();
	}
}