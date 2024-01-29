/***************************************************************************************************
 * Copyright (c) 2005, 2024 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Gorkem Ercan - initial API and implementation
 *
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;


public class JRESelectDecorator implements GenericServerCompositeDecorator {
	private List<IVMInstall> installedJREs;
	private String[] jreNames;
	private GenericServerRuntime fRuntime;
	private IWizardHandle fWizard;
	public JRESelectDecorator(GenericServerRuntime runtime, IWizardHandle wizardHandle){
		super();
		fRuntime = runtime;
		fWizard = wizardHandle;
	}

	public void decorate(final GenericServerComposite composite) {
		updateJREs();
		Link link = new Link(composite,SWT.NONE);
		link.setLayoutData(new GridData(SWT.FILL,SWT.NONE,true,false,3,1));
		link.setText(GenericServerUIMessages.installed_jre_link);

		Label label = new Label(composite, SWT.NONE);
		label.setText(GenericServerUIMessages.jre_select_label);

		final Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setItems(jreNames);
		GridData data = new GridData(SWT.FILL,SWT.NONE,false,false,2,1);

		combo.setLayoutData(data);

		combo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				int sel = combo.getSelectionIndex();
				IVMInstall vmInstall = null;
				if (sel > 0)
					vmInstall = installedJREs.get(sel - 1);

				fRuntime.setVMInstall(vmInstall);
				validate();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String currentVM = combo.getText();
				if (showPreferencePage(composite)) {
					updateJREs();
					combo.setItems(jreNames);
					combo.setText(currentVM);
					if (combo.getSelectionIndex() == -1)
						combo.select(0);
				}
			}
		});

		if (fRuntime!=null){
			if(fRuntime.isUsingDefaultJRE()){
				combo.select(0);
			}else{
				combo.setText(fRuntime.getVMInstall().getName());
			}
		}
	}


	protected boolean showPreferencePage(GenericServerComposite composite) {
		PreferenceManager manager = PlatformUI.getWorkbench().getPreferenceManager();
		IPreferenceNode node = manager.find("org.eclipse.jdt.ui.preferences.JavaBasePreferencePage").findSubNode("org.eclipse.jdt.debug.ui.preferences.VMPreferencePage");  //$NON-NLS-1$//$NON-NLS-2$
		PreferenceManager manager2 = new PreferenceManager();
		manager2.addToRoot(node);
		final PreferenceDialog dialog = new PreferenceDialog(composite.getShell(), manager2);
		final boolean[] result = new boolean[] { false };
		BusyIndicator.showWhile(composite.getDisplay(), new Runnable() {
			public void run() {
				dialog.create();
				if (dialog.open() == Window.OK)
					result[0] = true;
			}
		});
		return result[0];
	}

	protected void updateJREs() {
		installedJREs = new ArrayList<>();
		IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();
		int size = vmInstallTypes.length;
		for (int i = 0; i < size; i++) {
			IVMInstall[] vmInstalls = vmInstallTypes[i].getVMInstalls();
			int size2 = vmInstalls.length;
			for (int j = 0; j < size2; j++) {
				installedJREs.add(vmInstalls[j]);
			}
		}

		size = installedJREs.size();
		jreNames = new String[size+1];
		jreNames[0] = GenericServerUIMessages.defaultJRE;
		for (int i = 0; i < size; i++) {
			IVMInstall vmInstall = installedJREs.get(i);
			jreNames[i+1] = vmInstall.getName();
		}
	}



	public boolean validate() {
	    IStatus status = fRuntime.validate();
	    if( status.getSeverity() != IStatus.OK )
	    {
	        fWizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
	        fWizard.update();
	        return true;
	    }

	    fWizard.setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
        fWizard.update();
        return false;

	}
}
