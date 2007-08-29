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
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;

import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
/**
 * A composite used to select a hostname.
 */
public class HostnameComposite extends Composite {
	private static final String LOCALHOST = "localhost";
	protected String host;
	protected IHostnameSelectionListener listener;
	
	protected Combo combo;
	
	public interface IHostnameSelectionListener {
		public void hostnameSelected(String host);
	}

	/**
	 * Create a new HostnameComposite.
	 * 
	 * @param parent a parent composite
	 * @param listener2 a hostname selection listener
	 */
	public HostnameComposite(Composite parent, IHostnameSelectionListener listener2) {
		super(parent, SWT.NONE);
		this.listener = listener2;
		
		createControl();
	}

	/**
	 * Creates the UI of the page.
	 */
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 3;
		setLayout(layout);
		//WorkbenchHelp.setHelp(this, ContextIds.SELECT_CLIENT_WIZARD);
		
		Label label = new Label(this, SWT.WRAP);
		label.setText(Messages.hostname);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		List<String> hosts = ServerUIPlugin.getPreferences().getHostnames();
		String[] s = new String[hosts.size()];
		hosts.toArray(s);
		
		combo = new Combo(this, SWT.DROP_DOWN);
		combo.setItems(s);
		combo.setText(LOCALHOST);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = 2;
		combo.setLayoutData(data);
		
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				hostnameChanged(combo.getText());
			}
		});
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Point p = combo.getSelection();
				hostnameChanged(combo.getText());
				combo.setSelection(p);
			}
		});
	
		Dialog.applyDialogFont(this);
	}

	protected void hostnameChanged(String newHost) {
		if (newHost == null)
			return;
		
		if (newHost.equals(host))
			return;

		host = newHost;
		listener.hostnameSelected(host);
	}

	public String getHostname() {
		return host;
	}

	public void setHostname(String hostname) {
		combo.setText(hostname);
	}
}