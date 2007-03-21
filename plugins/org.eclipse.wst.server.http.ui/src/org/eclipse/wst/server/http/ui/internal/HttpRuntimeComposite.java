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
package org.eclipse.wst.server.http.ui.internal;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.http.core.internal.IHttpRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * Wizard page to set the server install directory.
 */
public class HttpRuntimeComposite extends Composite {
	protected IRuntimeWorkingCopy runtimeWC;
	protected IHttpRuntimeWorkingCopy runtime;
	protected Text name;
	protected Text prefix;
	protected Text port;
	protected Combo combo;
	protected Button publishCheckBox;
	protected Text publishDir;
	protected Button browseButton;
	protected Label locationLabel;
	protected Label portLabel;
	protected Label prefixLabel;
	//private ValuesCache originalValuesCache = new ValuesCache();
	//protected ValuesCache modifiedValuesCache;
	protected IWizardHandle wizard;

	/**
	 * ServerCompositeFragment
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
			runtime = (IHttpRuntimeWorkingCopy) newRuntime.loadAdapter(IHttpRuntimeWorkingCopy.class, null);
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
		nameGroup.setLayout(layout);
		nameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, ContextIds.RUNTIME_COMPOSITE);

		Label label = new Label(nameGroup, SWT.NONE);
		label.setText(Messages.runtimeName);
		GridData data = new GridData();
		label.setLayoutData(data);

		name = new Text(nameGroup, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		name.setLayoutData(data);
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				runtimeWC.setName(name.getText());
				validate();
			}
		});

		createServerInfoGroup(this);
		createPublishInfoGroup(this);
		init();
		validate();

		Dialog.applyDialogFont(this);

		name.forceFocus();
	}

	protected void init() {
		if (name == null || runtimeWC == null || runtime == null)
			return;
		
		name.setText(runtimeWC.getName());
		publishDir.setText(runtime.getPublishLocation());
		port.setText(runtime.getPort() + "");
		prefix.setText(runtime.getPrefixPath());
		
		boolean canPublish = runtime.publishToDirectory();
		publishCheckBox.setSelection(canPublish);
		publishDir.setEnabled(canPublish);
		browseButton.setEnabled(canPublish);
		locationLabel.setEnabled(canPublish);
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

	private void createServerInfoGroup(Composite parent) {
		Font font = parent.getFont();
		// Server information group
		Group serverInfoGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		serverInfoGroup.setLayout(layout);
		serverInfoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		serverInfoGroup.setFont(font);
		serverInfoGroup.setText("Server Information");
		
		// port label
		portLabel = new Label(serverInfoGroup, SWT.NONE);
		portLabel.setFont(font);
		portLabel.setText("HTTP Port:");
		
		// port entry field
		port = new Text(serverInfoGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 305;
		port.setLayoutData(data);
		port.setFont(font);
		port.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (runtime != null)
					try {
						runtime.setPort(Integer.parseInt(port.getText()));
					} catch (Exception ex) {
						// ignore
					}
				validate();
			}
		});

		// prefix label
		prefixLabel = new Label(serverInfoGroup, SWT.NONE);
		prefixLabel.setFont(font);
		prefixLabel.setText("URL Prefix Path:");

		// prefix entry field
		prefix = new Text(serverInfoGroup, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 305;
		prefix.setLayoutData(data);
		prefix.setFont(font);
		prefix.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				runtime.setPrefixPath(prefix.getText());
				validate();
			}
		});
	}

	private final void createPublishInfoGroup(Composite parent) {
		Font font = parent.getFont();
		// publish information group
		Group publishInfoGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		publishInfoGroup.setLayout(layout);
		publishInfoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		publishInfoGroup.setFont(font);
		publishInfoGroup.setText("Publish Information");

		publishCheckBox = new Button(publishInfoGroup, SWT.CHECK | SWT.RIGHT);
		publishCheckBox.setText("Publish Projects to this Server");
		publishCheckBox.setFont(font);

		publishCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				Button b = (Button) se.getSource();
				boolean selected = b.getSelection();

				publishDir.setEnabled(selected);
				browseButton.setEnabled(selected);
				locationLabel.setEnabled(selected);
				//publishDir.setText("");
				runtime.setPublishToDirectory(selected);
				validate();
			}
		});

		GridData buttonData = new GridData();
		buttonData.horizontalSpan = 3;
		publishCheckBox.setLayoutData(buttonData);

		createPublishLocationGroup(publishInfoGroup);
	}

	private void createPublishLocationGroup(Composite publishInfoGroup) {
		Font font = publishInfoGroup.getFont();
		// location label
		locationLabel = new Label(publishInfoGroup, SWT.NONE);
		locationLabel.setFont(font);
		locationLabel.setText("Directory:");

		// project location entry field
		publishDir = new Text(publishInfoGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 305;
		publishDir.setLayoutData(data);
		publishDir.setFont(font);
		publishDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				runtime.setPublishLocation(publishDir.getText());
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
		IRuntime[] allRuntimes = ServerCore.getRuntimes();
		
		if (allRuntimes != null) {
			int size = allRuntimes.length;
			for (int i = 0; i < size; i++) {
				IRuntime runtime2 = allRuntimes[i];
				if (name2.equals(runtime2.getName()))
					return false;
			}
		}
		return true;
	}
}