/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.wst.server.core.IServer;
/**
 * 
 */
public class ModuleSloshAction extends AbstractServerAction {
	public ModuleSloshAction(Shell shell, ISelectionProvider selectionProvider, String name) {
		super(shell, selectionProvider, name);
	}

	/**
	 * Return true if this server can currently be acted on.
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public boolean accept(IServer server) {
		return true;
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void perform(final IServer server) {
		/*if (!ServerUIUtil.promptIfDirty(shell, server))
			return;

		IServerConfiguration configuration = ServerUtil.getServerConfiguration(server);
		ModifyModulesWizard wizard = new ModifyModulesWizard(configuration);
		ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
		if (dialog.open() == Window.CANCEL)
			return;*/
	}
}