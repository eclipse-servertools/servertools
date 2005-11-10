/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.swt.widgets.Shell;
/**
 * Action for removing a module from a server.
 */
public class RemoveModuleAction extends Action {
	protected IServer server;
	protected IModule module;
	protected Shell shell;

	/**
	 * RemoveModuleAction constructor.
	 * 
	 * @param shell a shell
	 * @param server a server
	 * @param module a module
	 */
	public RemoveModuleAction(Shell shell, IServer server, IModule module) {
		super(Messages.actionRemove);
		this.shell = shell;
		
		this.server = server;
		this.module = module;
	}

	/**
	 * Invoked when an action occurs. 
	 */
	public void run() {
		if (MessageDialog.openConfirm(shell, Messages.defaultDialogTitle, Messages.dialogRemoveModuleConfirm)) {
			try {
				IServerWorkingCopy wc = server.createWorkingCopy();
				wc.modifyModules(null, new IModule[] { module }, null);
				wc.save(true, null);
			} catch (Exception e) {
				// ignore
			}
		}
	}
}