/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.page;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
/**
 * A wizard page used to select a server client.
 */
public class SelectClientComposite extends Composite {
	protected IWizardHandle wizard;
	protected TaskModel taskModel;

	// the list of elements to select from
	protected IClient[] clients;

	// the currently selected element
	protected IClient selectedClient;

	// the table containing the elements
	protected Table elementTable;

	// the description of the selected client
	protected Label description;

	/**
	 * Create a new SelectClientComposite.
	 * 
	 * @param parent a parent composite
	 * @param wizard a wizard handle
	 * @param taskModel a task model
	 */
	public SelectClientComposite(Composite parent, IWizardHandle wizard, TaskModel taskModel) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		this.taskModel = taskModel;
		try {
			clients = (IClient[]) taskModel.getObject(WizardTaskUtil.TASK_CLIENTS);
		} catch (Exception e) {
			// ignore
		}
	
		wizard.setTitle(Messages.wizSelectClientTitle);
		wizard.setDescription(Messages.wizSelectClientDescription);
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_SELECT_SERVER_CLIENT));
		
		createControl();
	}

	/**
	 * Creates the UI of the page.
	 */
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		setLayout(layout);

		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(this, ContextIds.SELECT_CLIENT_WIZARD);
	
		Label label = new Label(this, SWT.WRAP);
		label.setText(Messages.wizSelectClientMessage);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		label.setLayoutData(data);
	
		elementTable = new Table(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		data.heightHint = 80;
		data.horizontalIndent = 20;
		elementTable.setLayoutData(data);
		elementTable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				handleSelection();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelection();
				//TODO: WizardUtil.defaultSelect(getWizard(), SelectClientWizardPage.this);
			}
		});
		whs.setHelp(elementTable, ContextIds.SELECT_CLIENT);
	
		if (clients != null) {
			ILabelProvider labelProvider = ServerUICore.getLabelProvider();
			int size = clients.length;
			for (int i = 0; i < size; i++) {
				TableItem item = new TableItem(elementTable, SWT.NONE);
				item.setText(0, labelProvider.getText(clients[i]));
				item.setImage(0, labelProvider.getImage(clients[i]));
				item.setData(clients[i]);
			}
			labelProvider.dispose();
		}
		
		description = new Label(this, SWT.WRAP);
		description.setText("");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.heightHint = 70;
		description.setLayoutData(data);
	
		Dialog.applyDialogFont(this);
	}

	/**
	 * Handle the selection of a client.
	 */
	protected void handleSelection() {
		int index = elementTable.getSelectionIndex();
		if (index < 0)
			selectedClient = null;
		else
			selectedClient = clients[index];
		
		taskModel.putObject(WizardTaskUtil.TASK_CLIENT, selectedClient);
		if (selectedClient != null)
			wizard.setMessage(null, IMessageProvider.NONE);
		else
			wizard.setMessage("", IMessageProvider.ERROR);
		
		String desc = null;
		if (selectedClient != null)
			desc = selectedClient.getDescription();
		if (desc == null)
			desc = "";
		description.setText(desc);
		
		wizard.update();
	}
}