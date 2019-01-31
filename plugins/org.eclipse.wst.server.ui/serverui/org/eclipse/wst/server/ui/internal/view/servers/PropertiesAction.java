/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Action to show the property page for a server.
 */
public class PropertiesAction extends AbstractServerAction {
	protected String propertyPageId;

	public PropertiesAction(Shell shell, ISelectionProvider selectionProvider) {
		this(shell, null, selectionProvider);
	}

	public PropertiesAction(Shell shell, String propertyPageId, ISelectionProvider selectionProvider) {
		super(shell, selectionProvider, Messages.actionProperties);
		this.propertyPageId = propertyPageId;
		if (propertyPageId == null)
			setActionDefinitionId(IWorkbenchActionDefinitionIds.PROPERTIES);
		
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
	}

	public void perform(IServer server) {
		if (propertyPageId == null)
			propertyPageId = "org.eclipse.wst.server.ui.properties";
		Dialog dialog = PreferencesUtil.createPropertyDialogOn(shell, server, propertyPageId, null, null);
		dialog.open();
	}
}