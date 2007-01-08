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
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
/**
 * PropertyPage for servers.
 */
public class ServerPropertyPage extends PropertyPage {
	protected IServer server;

	protected IServer defaultServer;

	/**
	 * ServerPropertyPage constructor comment.
	 */
	public ServerPropertyPage() {
		super();
	}

	/**
	 * Create the body of the page.
	 *
	 * @param parent org.eclipse.swt.widgets.Composite
	 * @return org.eclipse.swt.widgets.Control
	 */
	protected Control createContents(Composite parent) {
		try {
			IAdaptable element = getElement();
			server = (IServer) element.getAdapter(IServer.class);
			
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 2;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			// name
			Label label = new Label(composite, SWT.NONE);
			label.setText(Messages.propServerInfoName);
			
			label = new Label(composite, SWT.NONE);
			label.setText(server.getName());
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			// type
			label = new Label(composite, SWT.NONE);
			label.setText(Messages.propServerInfoType);
			
			IServerType serverType = server.getServerType();
			label = new Label(composite, SWT.NONE);
			if (serverType != null)
				label.setText(serverType.getName());
			else
				label.setText(Messages.elementUnknownName);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			// provider
			label = new Label(composite, SWT.NONE);
			label.setText(Messages.propServerInfoVendor);
			
			IRuntimeType runtimeType = null;
			if (serverType != null)
				runtimeType = serverType.getRuntimeType();
			label = new Label(composite, SWT.NONE);
			if (runtimeType != null)
				label.setText(runtimeType.getVendor());
			else
				label.setText(Messages.elementUnknownName);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Dialog.applyDialogFont(composite);
			
			return composite;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error creating property page", e);
			return null;
		}
	}

	protected void performDefaults() {
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		return super.performOk();
	}
}