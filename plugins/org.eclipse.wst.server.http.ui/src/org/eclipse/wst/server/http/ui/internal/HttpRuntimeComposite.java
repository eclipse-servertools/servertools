/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.http.ui.internal;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.http.core.internal.HttpRuntime;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * Wizard page to set the server install directory.
 */
public class HttpRuntimeComposite extends Composite {
	protected IRuntimeWorkingCopy runtimeWC;
	protected HttpRuntime runtime;
	protected Text name;
	protected Text publishDir;
	protected Button browseButton;
	protected IWizardHandle wizard;

	/**
	 * HttpRuntimeComposite
	 * 
	 * @param parent the parent composite
	 * @param wizard the wizard handle
	 */
	public HttpRuntimeComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		wizard.setTitle(Messages.wizardTitle);
		wizard.setDescription(Messages.wizardDescription);
		wizard.setImageDescriptor(HttpUIPlugin.getImageDescriptor(HttpUIPlugin.IMG_WIZ_SERVER));

		createControl();
	}

	protected void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			runtimeWC = null;
			runtime = null;
		} else {
			runtimeWC = newRuntime;
			runtime = (HttpRuntime) newRuntime.loadAdapter(HttpRuntime.class, null);
		}
		
		init();
		validate();
	}

	/**
	 * Provide a wizard page to change the Apache installation directory.
	 */
	protected void createControl() {
		GridLayout layout = new GridLayout(1, true);
		setLayout(layout);
		setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite nameGroup = new Composite(this, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		nameGroup.setLayout(layout);
		nameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, ContextIds.RUNTIME_COMPOSITE);
		
		Label label = new Label(nameGroup, SWT.NONE);
		label.setText(Messages.runtimeName);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END);
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		
		name = new Text(nameGroup, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		name.setLayoutData(data);
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				runtimeWC.setName(name.getText());
				validate();
			}
		});
		
		createPublishLocationGroup(nameGroup);
		
		init();
		validate();
		
		Dialog.applyDialogFont(this);
		
		name.forceFocus();
	}

	protected void init() {
		if (name == null || runtimeWC == null || runtime == null)
			return;
		
		name.setText(runtimeWC.getName());
		if (runtimeWC.getLocation() != null)
			publishDir.setText(runtimeWC.getLocation().toOSString());
	}

	protected void validate() {
		if (runtime == null) {
			wizard.setMessage("", IMessageProvider.ERROR);
			return;
		}
		
		wizard.setMessage(null, IMessageProvider.NONE);
		
		if (runtimeWC != null) {
			String name2 = runtimeWC.getName();
			if (name2 == null || name2.trim().equals("")) {
				wizard.setMessage(Messages.wizardMissingRuntimeName, IMessageProvider.ERROR);
			} else {
				boolean ok = checkRuntimeName(name2);
				if (!ok) {
					wizard.setMessage(Messages.wizardDuplicateName, IMessageProvider.ERROR);
				}
			}
		}
	}

	private void createPublishLocationGroup(Composite publishInfoGroup) {
		Font font = publishInfoGroup.getFont();
		// location label
		Label locationLabel = new Label(publishInfoGroup, SWT.NONE);
		locationLabel.setFont(font);
		locationLabel.setText(Messages.publishDir);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END);
		data.horizontalSpan = 2;
		locationLabel.setLayoutData(data);
		
		// project location entry field
		publishDir = new Text(publishInfoGroup, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		publishDir.setLayoutData(data);
		publishDir.setFont(font);
		publishDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				runtimeWC.setLocation(new Path(publishDir.getText()));
				validate();
			}
		});
		
		// browse button
		browseButton = new Button(publishInfoGroup, SWT.PUSH);
		browseButton.setFont(font);
		browseButton.setText(Messages.browse);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dialog = new DirectoryDialog(HttpRuntimeComposite.this.getShell());
				dialog.setMessage(Messages.selectInstallDir);
				dialog.setFilterPath(publishDir.getText());
				String selectedDirectory = dialog.open();
				if (selectedDirectory != null)
					publishDir.setText(selectedDirectory);
			}
		});
	}

	private boolean checkRuntimeName(String name2) {
		name2 = name2.trim();
		if (name2.equals(runtimeWC.getName())) {
			return true;
		}
		
		IRuntime[] runtimes = ServerCore.getRuntimes();
		if (runtimes != null) {
			for (IRuntime runtime2 : runtimes) {
				if (name2.equals(runtime2.getName()))
					return false;
			}
		}
		return true;
	}
}