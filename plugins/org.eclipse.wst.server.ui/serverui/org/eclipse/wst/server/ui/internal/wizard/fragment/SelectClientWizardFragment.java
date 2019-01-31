/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.internal.wizard.page.SelectClientComposite;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * A fragment used to select a client.
 */
public class SelectClientWizardFragment extends WizardFragment {
	public SelectClientWizardFragment() {
		super();
	}

	public boolean hasComposite() {
		return true;
	}

	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		return new SelectClientComposite(parent, wizard, getTaskModel());
	}

	public boolean isComplete() {
		try {
			IClient[] clients = (IClient[]) getTaskModel().getObject(WizardTaskUtil.TASK_CLIENTS);
			if (clients == null || clients.length < 2)
				return true;
		} catch (Exception e) {
			return true;
		}
		
		return getTaskModel().getObject(WizardTaskUtil.TASK_CLIENT) != null;
	}
}