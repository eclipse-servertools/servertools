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
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.*;
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
	 */
	public SelectClientComposite(Composite parent, IWizardHandle wizard, IClient[] clients) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		this.clients = clients;
	
		wizard.setTitle(ServerUIPlugin.getResource("%wizSelectClientTitle"));
		wizard.setDescription(ServerUIPlugin.getResource("%wizSelectClientDescription"));
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_SELECT_SERVER_CLIENT));
		
		createControl();
	}

	/**
	 * Clears the selected client.
	 */
	public void clearSelectedClient() {
		selectedClient = null;
	}

	/**
	 * Creates the UI of the page.
	 */
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);

		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(this, ContextIds.SELECT_CLIENT_WIZARD);
	
		Label label = new Label(this, SWT.WRAP);
		label.setText(ServerUIPlugin.getResource("%wizSelectClientMessage"));
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
			int size = clients.length;
			for (int i = 0; i < size; i++) {
				TableItem item = new TableItem(elementTable, SWT.NONE);
				item.setText(0, ServerUICore.getLabelProvider().getText(clients[i]));
				item.setImage(0, ServerUICore.getLabelProvider().getImage(clients[i]));
				item.setData(clients[i]);
			}
		}
	
		description = new Label(this, SWT.WRAP);
		description.setText("");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.heightHint = 70;
		description.setLayoutData(data);
	
		Dialog.applyDialogFont(this);
	}

	/**
	 * Return the selected client.
	 *
	 * @return org.eclipse.wst.server.core.IClient
	 */
	public IClient getSelectedClient() {
		return selectedClient;
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