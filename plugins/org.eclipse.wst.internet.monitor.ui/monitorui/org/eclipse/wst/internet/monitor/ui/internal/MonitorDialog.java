/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal;

import java.net.InetAddress;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.internet.monitor.core.IMonitorWorkingCopy;
import org.eclipse.wst.internet.monitor.core.IProtocolAdapter;
import org.eclipse.wst.internet.monitor.core.MonitorCore;
/**
 * 
 */
public class MonitorDialog extends Dialog {
	protected IMonitorWorkingCopy monitor;
	protected boolean isEdit;
	
	private Button okButton;
	private Text monitorPort;
	private Text remoteHostname;
	private Text remotePort;
	
	interface StringModifyListener {
		public void valueChanged(String s);
	}
	
	interface BooleanModifyListener {
		public void valueChanged(boolean b);
	}
	
	interface TypeModifyListener {
		public void valueChanged(IProtocolAdapter type);
	}

	/**
	 * @param parentShell
	 */
	public MonitorDialog(Shell parentShell, IMonitorWorkingCopy monitor) {
		super(parentShell);
		this.monitor = monitor;
		isEdit = true;
	}
	
	public MonitorDialog(Shell parentShell) {
		super(parentShell);
		monitor = MonitorCore.createMonitor();
		isEdit = false;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (isEdit)
			shell.setText(MonitorUIPlugin.getResource("%editMonitor"));
		else
			shell.setText(MonitorUIPlugin.getResource("%newMonitor"));
	}
	
	protected Label createLabel(Composite comp, String txt) {
		Label label = new Label(comp, SWT.NONE);
		label.setText(txt);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
		return label;
	}
	
	protected Text createText(Composite comp, String txt, final StringModifyListener listener) {
		final Text text = new Text(comp, SWT.BORDER);
		if (txt != null)
			text.setText(txt);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.widthHint = 150;
		text.setLayoutData(data);
		if (listener != null)
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {	
					listener.valueChanged(text.getText());
				}
			});
		return text;
	}
	
	protected Combo createTypeCombo(Composite comp, final IProtocolAdapter[] types, IProtocolAdapter sel, final TypeModifyListener listener) {
		final Combo combo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		int size = types.length;
		String[] items = new String[size];
		int index = -1;
		for (int i = 0; i < size; i++) {
			items[i] = types[i].getName();
			if (types[i].equals(sel))
				index = i;
		}
		combo.setItems(items);
		if (index >= 0)
			combo.select(index);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.widthHint = 150;
		combo.setLayoutData(data);
		if (listener != null)
			combo.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {	
					listener.valueChanged(types[combo.getSelectionIndex()]);
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
		return combo;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		((GridLayout)composite.getLayout()).numColumns = 2;
		
		WorkbenchHelp.setHelp(composite, ContextIds.PREF_DIALOG);
		
		createLabel(composite, MonitorUIPlugin.getResource("%localPort"));		
		monitorPort = createText(composite, monitor.getLocalPort() + "", new StringModifyListener() {
			public void valueChanged(String s) {
				try {
					monitor.setLocalPort(Integer.parseInt(s));
				} catch (Exception e) {
					// ignore
				}
				validateFields();
			}
		});
		
		Group group = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		group.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		group.setLayoutData(data);
		group.setText(MonitorUIPlugin.getResource("%remoteGroup"));
		
		createLabel(group, MonitorUIPlugin.getResource("%remoteHost"));		
		remoteHostname = createText(group, monitor.getRemoteHost(), new StringModifyListener() {
			public void valueChanged(String s) {
				monitor.setRemoteHost(s);
				validateFields();
			}
		});
		
		createLabel(group, MonitorUIPlugin.getResource("%remotePort"));		
		remotePort = createText(group, monitor.getRemotePort() + "", new StringModifyListener() {
			public void valueChanged(String s) {
				try {
					monitor.setRemotePort(Integer.parseInt(s));
				} catch (Exception e) {
					// ignore
				}
				validateFields();
			}
		});
		
		createLabel(group, MonitorUIPlugin.getResource("%parseType"));		
		createTypeCombo(group, MonitorCore.getProtocolAdapters(), monitor.getProtocolAdapter(), new TypeModifyListener() {
			public void valueChanged(IProtocolAdapter type) {
				monitor.setProtocolAdapter(type);
			}
		});
		
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		monitor.save();
		super.okPressed();
	}

	protected Control createButtonBar(Composite parent) {
		Control buttonControl = super.createButtonBar(parent);
		validateFields();
		return buttonControl;
	}

	private void setOKButtonEnabled(boolean curIsEnabled) {
		if (okButton == null)
			okButton = getButton(IDialogConstants.OK_ID);
		
		if (okButton != null)
			okButton.setEnabled(curIsEnabled);
	}

	protected void validateFields() {
		if (monitorPort == null)
			return;
		
		boolean result = true;

		String currHostname = remoteHostname.getText();
		if (!isValidHostname(currHostname))
			result = false;
		
		String currHostnamePort = remotePort.getText();
		try {
			Integer.parseInt(currHostnamePort);
		} catch (Exception any) {
			result = false;
		}
		
		String currMonitorPort = monitorPort.getText();
		try {
			Integer.parseInt(currMonitorPort);
		} catch (Exception any) {
			result = false;
		}
		
		if (result && isLocalhost(currHostname)) {
			if (currHostnamePort.equals(currMonitorPort))
				result = false;
		}
		setOKButtonEnabled(result);
	}
	
	protected static boolean isValidHostname(String host) {
		if (host == null || host.trim().length() < 1)
			return false;
		
		int length = host.length();
		for (int i = 0; i < length; i++) {
			char c = host.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != ':' && c != '.')
				return false;
		}
		if (host.endsWith(":"))
			return false;
		return true;
	}

	protected static boolean isLocalhost(String host) {
		if (host == null)
			return false;
		try {
			if ("localhost".equals(host) || "127.0.0.1".equals(host))
				return true;
			InetAddress localHostaddr = InetAddress.getLocalHost();
			if (localHostaddr.getHostName().equals(host))
				return true;
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error checking for localhost", e);
		}
		return false;
	}
}