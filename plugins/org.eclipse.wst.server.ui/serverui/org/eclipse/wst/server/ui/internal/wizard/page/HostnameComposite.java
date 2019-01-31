/*******************************************************************************
 * Copyright (c) 2003, 2009 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextContentAdapter;

import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
/**
 * A composite used to select a hostname.
 */
public class HostnameComposite extends Composite {
	public static final String LOCALHOST = "localhost";
	protected String host;
	protected IHostnameSelectionListener listener;
	
	protected Text hostname;
	
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
		
		hostname = new Text(this, SWT.BORDER);
		hostname.setText(LOCALHOST);
		final ControlDecoration hostnameDecoration = new ControlDecoration(hostname, SWT.TOP | SWT.LEAD);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = 2;
		hostname.setLayoutData(data);
		
		hostname.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				hostnameChanged(hostname.getText());
			}
		});
		
		FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
		FieldDecoration fd = registry.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
		hostnameDecoration.setImage(fd.getImage());
		hostnameDecoration.setDescriptionText(fd.getDescription());
		
		hostname.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				hostnameDecoration.show();
			}

			public void focusLost(FocusEvent e) {
				hostnameDecoration.hide();
			}
		});
		
		List<String> hosts = ServerUIPlugin.getPreferences().getHostnames();
		String[] hosts2 = hosts.toArray(new String[hosts.size()]);
		new AutoCompleteField(hostname, new TextContentAdapter(), hosts2);
		
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

	public void setHostname(String newHostname) {
		hostname.setText(newHostname);
	}
}
