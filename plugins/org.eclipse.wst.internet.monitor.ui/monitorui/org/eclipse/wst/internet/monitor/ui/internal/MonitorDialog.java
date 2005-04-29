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
package org.eclipse.wst.internet.monitor.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.internet.monitor.core.IMonitorWorkingCopy;
import org.eclipse.wst.internet.monitor.core.MonitorCore;
import org.eclipse.wst.internet.monitor.core.internal.IProtocolAdapter;
/**
 * 
 */
public class MonitorDialog extends Dialog {
	protected IMonitorWorkingCopy monitor;
	protected boolean isEdit;
	
	private Button okButton;
	private Text monitorPort;
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
	 * Create a new monitor dialog.
	 * 
	 * @param parentShell
	 * @param monitor
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
			shell.setText(Messages.editMonitor);
		else
			shell.setText(Messages.newMonitor);
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
	
	protected Combo createTypeCombo(Composite comp, final String[] types, String sel, final StringModifyListener listener) {
		final Combo combo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		int size = types.length;
		String[] items = new String[size];
		int index = -1;
		for (int i = 0; i < size; i++) {
			items[i] = types[i];
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
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ContextIds.PREF_DIALOG);
		
		createLabel(composite, Messages.localPort);		
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
		group.setText(Messages.remoteGroup);
		
		createLabel(group, Messages.remoteHost);		
		createText(group, monitor.getRemoteHost(), new StringModifyListener() {
			public void valueChanged(String s) {
				monitor.setRemoteHost(s);
				validateFields();
			}
		});
		
		createLabel(group, Messages.remotePort);		
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
		
		createLabel(group, Messages.parseType);		
		createTypeCombo(group, new String[] {"TCP/IP","HTTP"}, monitor.getProtocol(), new StringModifyListener() {
			public void valueChanged(String protocolId) {
				monitor.setProtocol(protocolId);
			}
		});
		
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		try {
			monitor.save();
		} catch (CoreException ce) {
			ErrorDialog.openError(getShell(), Messages.errorDialogTitle, ce.getLocalizedMessage(), ce.getStatus());
			return;
		}
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
		try {
			Integer.parseInt(remotePort.getText());
			Integer.parseInt(monitorPort.getText());
		} catch (Exception e) {
			result = false;
		}
		
		IStatus status = monitor.validate();
		if (!status.isOK())
			result = false;
		
		setOKButtonEnabled(result);
	}
}