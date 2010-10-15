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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.internal.Server;
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
			layout.numColumns = 3;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
			whs.setHelp(composite, ContextIds.SERVER_PROPERTY_PAGE);			
			
			// name
			Label label = new Label(composite, SWT.NONE);
			label.setText(Messages.propServerInfoName);
			
			label = new Label(composite, SWT.NONE);
			label.setText(server.getName());
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			label.setLayoutData(data);
			
			// type
			label = new Label(composite, SWT.NONE);
			label.setText(Messages.propServerInfoType);
			
			IServerType serverType = server.getServerType();
			label = new Label(composite, SWT.NONE);
			if (serverType != null)
				label.setText(serverType.getName());
			else
				label.setText(Messages.elementUnknownName);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			label.setLayoutData(data);
			
			// vendor
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
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			label.setLayoutData(data);
			
			// location
			label = new Label(composite, SWT.NONE);
			label.setText(Messages.switchServerLocation);
			label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
			
			final Label serverLocation = new Label(composite, SWT.NONE);
			final Server svr = (Server) server;
			if (svr.getFile() != null)
				serverLocation.setText(svr.getFile().getFullPath().toPortableString());
			else
				serverLocation.setText(Messages.switchServerLocationMetadata);
			
			serverLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
			
			Button switchLocation = new Button(composite, SWT.PUSH);
			switchLocation.setText(Messages.actionSwitchServerLocation);
			switchLocation.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			switchLocation.setEnabled(!server.isReadOnly());
			switchLocation.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						Server.switchLocation(svr, null);
					} catch (CoreException ce) {
						Trace.trace(Trace.SEVERE, "Error switching server location", ce);
					}
					if (svr.getFile() != null)
						serverLocation.setText(svr.getFile().getFullPath().toPortableString());
					else
						serverLocation.setText(Messages.switchServerLocationMetadata);
				}
			});
			
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