package org.eclipse.wst.server.ui.internal.wizard.page;
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
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.ServerUIPreferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
	 * Create a new HostComposite.
	 */
	public HostnameComposite(Composite parent, IHostnameSelectionListener listener2) {
		super(parent, SWT.NONE);
		this.listener = listener2;
		
		createControl();
	}

	protected Label createHeadingLabel(Composite parent, String text, int span) {
		Label label = createLabel(parent, text, span, true, false);
		label.setFont(JFaceResources.getBannerFont());
		return label;
	}

	protected Label createLabel(Composite parent, String text, int span, boolean alignTop, boolean indent) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText(text);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		if (alignTop)
			data.verticalAlignment = GridData.BEGINNING;
		data.horizontalSpan = span;
		if (indent)
			data.horizontalIndent = 10;
		label.setLayoutData(data);
		return label;
	}
	
	protected Combo createCombo(Composite parent, String[] items, String text2, int span) {
		Combo combo2 = new Combo(parent, SWT.DROP_DOWN);
		combo2.setItems(items);
		combo2.setText(text2);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = span;
		combo2.setLayoutData(data);
		return combo2;
	}

	/**
	 * Creates the UI of the page.
	 *
	 * @param org.eclipse.swt.widgets.Composite parent
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
	
		createHeadingLabel(this, ServerUIPlugin.getResource("%hostnameTitle"), 3);

		createLabel(this, ServerUIPlugin.getResource("%hostname"), 1, false, true);
		
		List hosts = ((ServerUIPreferences)ServerUICore.getPreferences()).getHostnames();
		String[] s = new String[hosts.size()];
		hosts.toArray(s);
		combo = createCombo(this, s, LOCALHOST, 2);
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				hostnameChanged(combo.getText());
			}
		});
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				hostnameChanged(combo.getText());
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