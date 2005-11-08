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
package org.eclipse.jst.server.tomcat.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatRuntimeWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.internal.IInstallableRuntime;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * Wizard page to set the server install directory.
 */
public class TomcatRuntimeComposite extends Composite {
	protected IRuntimeWorkingCopy runtimeWC;
	protected ITomcatRuntimeWorkingCopy runtime;
	
	protected IWizardHandle wizard;
	
	protected Text installDir;
	protected Text name;
	protected Combo combo;
	protected List installedJREs;
	protected String[] jreNames;

	/**
	 * TomcatRuntimeWizardPage constructor comment.
	 * 
	 * @param parent the parent composite
	 * @param wizard the wizard handle
	 */
	protected TomcatRuntimeComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		
		wizard.setTitle(Messages.wizardTitle);
		wizard.setDescription(Messages.wizardDescription);
		wizard.setImageDescriptor(TomcatUIPlugin.getImageDescriptor(TomcatUIPlugin.IMG_WIZ_TOMCAT));
		
		createControl();
	}
	
	protected void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			runtimeWC = null;
			runtime = null;
		} else {
			runtimeWC = newRuntime;
			runtime = (ITomcatRuntimeWorkingCopy) newRuntime.loadAdapter(ITomcatRuntimeWorkingCopy.class, null);
		}
		
		init();
		validate();
	}

	/**
	 * Provide a wizard page to change the Tomcat installation directory.
	 */
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		setLayout(layout);
		setLayoutData(new GridData(GridData.FILL_BOTH));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, ContextIds.RUNTIME_COMPOSITE);
		
		Label label = new Label(this, SWT.NONE);
		label.setText(Messages.runtimeName);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		
		name = new Text(this, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		name.setLayoutData(data);
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				runtimeWC.setName(name.getText());
				validate();
			}
		});
	
		label = new Label(this, SWT.NONE);
		label.setText(Messages.installDir);
		data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);
	
		installDir = new Text(this, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		installDir.setLayoutData(data);
		installDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				runtimeWC.setLocation(new Path(installDir.getText()));
				validate();
			}
		});
		
		Button browse = SWTUtil.createButton(this, Messages.browse);
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				DirectoryDialog dialog = new DirectoryDialog(TomcatRuntimeComposite.this.getShell());
				dialog.setMessage(Messages.selectInstallDir);
				dialog.setFilterPath(installDir.getText());
				String selectedDirectory = dialog.open();
				if (selectedDirectory != null)
					installDir.setText(selectedDirectory);
			}
		});
		
		label = new Label(this, SWT.NONE);
		
		if (ServerPlugin.getInstallableRuntimes().length > 0) {
			Button install = SWTUtil.createButton(this, Messages.install);
			install.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					DirectoryDialog dialog = new DirectoryDialog(TomcatRuntimeComposite.this.getShell());
					dialog.setMessage(Messages.selectInstallDir);
					dialog.setFilterPath(installDir.getText());
					String selectedDirectory = dialog.open();
					if (selectedDirectory != null) {
						try {
							IInstallableRuntime ir = ServerPlugin.getInstallableRuntimes()[0];
							ir.install(new Path(selectedDirectory), new NullProgressMonitor());
						} catch (Exception e) {
							e.printStackTrace();
						}
						installDir.setText(selectedDirectory);
					}
				}
			});
		}
		
		updateJREs();
		
		// JDK location
		label = new Label(this, SWT.NONE);
		label.setText(Messages.installedJRE);
		data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		
		combo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setItems(jreNames);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		combo.setLayoutData(data);
		
		combo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				int sel = combo.getSelectionIndex();
				IVMInstall vmInstall = null;
				if (sel > 0)
					vmInstall = (IVMInstall) installedJREs.get(sel - 1);
				
				runtime.setVMInstall(vmInstall);
				validate();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		Button button = SWTUtil.createButton(this, Messages.installedJREs);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String currentVM = combo.getText();
				if (showPreferencePage()) {
					updateJREs();
					combo.setItems(jreNames);
					combo.setText(currentVM);
					if (combo.getSelectionIndex() == -1)
						combo.select(0);
				}
			}
		});
		
		init();
		validate();

		Dialog.applyDialogFont(this);

		name.forceFocus();
	}

	protected void updateJREs() {
		// get all installed JVMs
		installedJREs = new ArrayList();
		IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();
		int size = vmInstallTypes.length;
		for (int i = 0; i < size; i++) {
			IVMInstall[] vmInstalls = vmInstallTypes[i].getVMInstalls();
			int size2 = vmInstalls.length;
			for (int j = 0; j < size2; j++) {
				installedJREs.add(vmInstalls[j]);
			}
		}
		
		// get names
		size = installedJREs.size();
		jreNames = new String[size+1];
		jreNames[0] = Messages.runtimeDefaultJRE;
		for (int i = 0; i < size; i++) {
			IVMInstall vmInstall = (IVMInstall) installedJREs.get(i);
			jreNames[i+1] = vmInstall.getName();
		}
	}

	protected boolean showPreferencePage() {
		PreferenceManager manager = PlatformUI.getWorkbench().getPreferenceManager();
		IPreferenceNode node = manager.find("org.eclipse.jdt.ui.preferences.JavaBasePreferencePage").findSubNode("org.eclipse.jdt.debug.ui.preferences.VMPreferencePage");
		PreferenceManager manager2 = new PreferenceManager();
		manager2.addToRoot(node);
		final PreferenceDialog dialog = new PreferenceDialog(getShell(), manager2);
		final boolean[] result = new boolean[] { false };
		BusyIndicator.showWhile(getDisplay(), new Runnable() {
			public void run() {
				dialog.create();
				if (dialog.open() == Window.OK)
					result[0] = true;
			}
		});
		return result[0];
	}
	
	protected void init() {
		if (name == null || runtime == null)
			return;
		
		if (runtimeWC.getName() != null)
			name.setText(runtimeWC.getName());
		else
			name.setText("");
	
		if (runtimeWC.getLocation() != null)
			installDir.setText(runtimeWC.getLocation().toOSString());
		else
			installDir.setText("");
		
		// set selection
		if (runtime.isUsingDefaultJRE())
			combo.select(0);
		else {
			boolean found = false;
			int size = installedJREs.size();
			for (int i = 0; i < size; i++) {
				IVMInstall vmInstall = (IVMInstall) installedJREs.get(i);
				if (vmInstall.equals(runtime.getVMInstall())) {
					combo.select(i + 1);
					found = true;
				}
			}
			if (!found)
				combo.select(0);
		}
	}

	protected void validate() {
		if (runtime == null) {
			wizard.setMessage("", IMessageProvider.ERROR);
			return;
		}

		IStatus status = runtimeWC.validate(null);
		if (status == null || status.isOK())
			wizard.setMessage(null, IMessageProvider.NONE);
		else if (status.getSeverity() == IStatus.WARNING)
			wizard.setMessage(status.getMessage(), IMessageProvider.WARNING);
		else
			wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
		wizard.update();
	}
}