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
	public PropertiesAction(Shell shell, ISelectionProvider selectionProvider) {
		super(shell, selectionProvider, Messages.actionProperties);
		setActionDefinitionId(IWorkbenchActionDefinitionIds.PROPERTIES);
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
	}

	public void perform(IServer server) {
		Dialog dialog = PreferencesUtil.createPropertyDialogOn(shell, server, "org.eclipse.wst.server.ui.properties", null, null);
		dialog.open();
	}
}