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
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;

import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.viewers.MonitorComposite;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
/**
 * Preference page that allows a user to monitor ports from a server.
 */
public class MonitorPropertyPage extends PropertyPage {
	protected IServer server;
	protected MonitorComposite monitorComp;

	/**
	 * MonitorPropertyPage constructor comment.
	 */
	public MonitorPropertyPage() {
		super();
	}

	/**
	 * Create the body of the page.
	 *
	 * @param parent org.eclipse.swt.widgets.Composite
	 * @return org.eclipse.swt.widgets.Control
	 */
	protected Control createContents(Composite parent) {
		IAdaptable element = getElement();
		server = (IServer) element.getAdapter(IServer.class);
		
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 550;
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		//WorkbenchHelp.setHelp(composite, ContextIds.TERMINATE_SERVER_DIALOG);
		
		Label label = new Label(composite, SWT.WRAP);
		label.setText(NLS.bind(Messages.dialogMonitorDescription, new String[] { server.getName() } ));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		label.setLayoutData(data);
		
		monitorComp = new MonitorComposite(composite, SWT.NONE, null, server);
		monitorComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		
		Dialog.applyDialogFont(composite);
		
		return composite;
	}
}