/*******************************************************************************
 * Copyright (c) 2007,2008 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.model.ModuleArtifactDelegate;
import org.eclipse.wst.server.ui.internal.Messages;

public class ModuleArtifactComposite extends Dialog {
	protected IModuleArtifact[] moduleArtifacts;
	private ListViewer listViewer;
	private String launchMode;		
	private IModuleArtifact selection;

	/**
	 * Creates a Selection dialog with the list of available IModuleArtifact for the selected resource
	 * @param parent
	 * @param moduleArtifacts
	 * @param launchMode
	 */
	public ModuleArtifactComposite(Shell parent,final IModuleArtifact[] moduleArtifacts, String launchMode){
		super(parent);
		setShellStyle(SWT.RESIZE | getShellStyle());
		this.moduleArtifacts = moduleArtifacts;
		this.launchMode = launchMode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(getWindowTitle());
	}

	/**
	 * Based on the launch mode, return the NLS String for the Dialog.
	 * 
	 * @return the window title
	 */
	private String getWindowTitle() {
		String title = Messages.wizRunOnServerTitle;
		
		if (ILaunchManager.DEBUG_MODE.equals(launchMode))
			title = Messages.wizDebugOnServerTitle;					
		else if (ILaunchManager.PROFILE_MODE.equals(launchMode))
			title = Messages.wizProfileOnServerTitle;
		return title;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent,SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.verticalSpacing = 10;
		layout.numColumns = 1;
				
		GridData data = new GridData(GridData.FILL_BOTH);
		
		composite.setLayout(layout);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		
		Text description = new Text(composite,SWT.NONE);
		description.setText(Messages.wizModuleArtifactsDescription);
		description.setEditable(false);
		description.setCursor(composite.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
		
		createContent(composite);
		
		return composite;
	}

	private void createContent(Composite parent) {
		Composite contentComposite = new Composite(parent,SWT.NONE |SWT.RESIZE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;		
		layout.numColumns = 1;
				
		GridData data = new GridData(GridData.FILL_BOTH);
		
		contentComposite.setLayout(layout);
		contentComposite.setLayoutData(data);
		contentComposite.setFont(parent.getFont());
		
		Label tableTitle = new Label(contentComposite,SWT.None);
		tableTitle.setText(Messages.wizModuleArtifactsAvailableList);

		listViewer = new ListViewer(contentComposite,SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		listViewer.getList().setLayoutData(data);
		listViewer.getList().setFocus();

		listViewer.setContentProvider(new BaseContentProvider() {
			public Object[] getElements(Object inputElement) {
				return moduleArtifacts;
			}
		});
		
		listViewer.setLabelProvider(new BaseLabelProvider() {
			public String getText(Object element) {				
				if (element instanceof ModuleArtifactDelegate) {
					// Try to display the object using its name 
					ModuleArtifactDelegate moduleArtifact = (ModuleArtifactDelegate)element;
					String artifactName = moduleArtifact.getName();
					if (artifactName != null && artifactName.length() >= 0) {
						int classNameIndex = artifactName.lastIndexOf(".");
						String packageName = artifactName.substring(0, classNameIndex);
						String className = artifactName.substring(classNameIndex+1);
						if (packageName != null && (packageName.length()<=0) == false){
							return className + " ("+moduleArtifact.getName()+")";
						}
						return moduleArtifact.getName();
					}

					// If the name is empty we can then use the module artifact class name  
					return moduleArtifact.getClass().getName();				
				}
				return Messages.elementUnknownName;
			}
		});
		
		listViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				buttonPressed(IDialogConstants.OK_ID);
			}
		});
			
		listViewer.setInput(AbstractTreeContentProvider.ROOT);
	}

	/**
	 * Return the selection of the dialog
	 * 
	 * @return the selected module artifact
	 */
	public IModuleArtifact getSelection() {
		IStructuredSelection selection2 = (IStructuredSelection) listViewer.getSelection();
		if (selection2 == null || selection2.getFirstElement() == null)
			return selection;
		
		selection = (IModuleArtifact)selection2.getFirstElement(); 
		return selection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if (getSelection() == null){
				MessageBox messageBox = new MessageBox(getShell(),SWT.ICON_ERROR | SWT.OK);
				messageBox.setMessage(Messages.wizModuleArtifactsNoSelectionError);
				messageBox.setText(getWindowTitle());
				messageBox.open();
				// bypass the call to super, so that this Dialog doesn't get disposed
				return;
			}
		}
		super.buttonPressed(buttonId);
	}
}