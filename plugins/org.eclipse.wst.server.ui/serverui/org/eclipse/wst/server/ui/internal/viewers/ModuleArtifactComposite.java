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
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.model.ModuleArtifactDelegate;
import org.eclipse.wst.server.ui.internal.Messages;

public class ModuleArtifactComposite extends Dialog {
	protected IModuleArtifact[] moduleArtifacts;
	private TableViewer tableViewer;	
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
		description.setEnabled(true);
		description.setCursor(composite.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
		
		createTable(composite);
		
		return composite;
	}

	private void createTable(Composite parent) {
		Composite tableComposite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;		
		layout.numColumns = 1;
				
		GridData data = new GridData(GridData.FILL_BOTH);
		
		tableComposite.setLayout(layout);
		tableComposite.setLayoutData(data);
		tableComposite.setFont(parent.getFont());
		
		Label tableTitle = new Label(tableComposite,SWT.None);
		tableTitle.setText(Messages.wizModuleArtifactsAvailableList);
		
		Table table = new Table(tableComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.heightHint = 100;
		table.setLayoutData(data);
		table.setLinesVisible(true);
		table.setHeaderVisible(false);
		table.setFocus();
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		
		tableLayout.addColumnData(new ColumnWeightData(100, 100, true));
		new TableColumn(table, SWT.NONE);		
		
		tableViewer = new TableViewer(table);
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				getSelection();
			}
		});
		
		tableViewer.setLabelProvider(new BaseLabelProvider() {
			public String getText(Object element) {				
				if (element instanceof ModuleArtifactDelegate) {
					ModuleArtifactDelegate moduleArtifact = (ModuleArtifactDelegate)element;
					if (moduleArtifact.getName() == null || moduleArtifact.getName().length() == 0) {
						return moduleArtifact.getClass().getName();
					}
					return moduleArtifact.getName();
				}
				return Messages.elementUnknownName;
			}
		});
		
		tableViewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {				
				return moduleArtifacts;
			}

			public void dispose() {
				// nothing to do
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// nothing to do				
			}
		});
		
		tableViewer.setInput(AbstractTreeContentProvider.ROOT);
	}

	/**
	 * Return the selection of the dialog
	 * 
	 * @return the selected module artifact
	 */
	public IModuleArtifact getSelection() {
		IStructuredSelection selection2 = (IStructuredSelection) tableViewer.getSelection();
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