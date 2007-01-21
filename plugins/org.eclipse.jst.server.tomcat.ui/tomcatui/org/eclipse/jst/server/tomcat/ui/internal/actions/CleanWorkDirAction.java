/*******************************************************************************
 * Copyright (c) 2007 SAS Institute, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Larry Isaacs - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jst.server.tomcat.ui.internal.CleanWorkDirDialog;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

/**
 * @author larry
 *
 */
public class CleanWorkDirAction implements IObjectActionDelegate {
	private IWorkbenchPart targetPart;
	private IServer selectedServer;
	private IModule selectedModule;

	/**
	 * Constructor for Action1.
	 */
	public CleanWorkDirAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		CleanWorkDirDialog dlg = new CleanWorkDirDialog(targetPart.getSite().getShell(), selectedServer, selectedModule);
		dlg.open();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		selectedServer = null;
		selectedModule = null;
		if (!selection.isEmpty()) {
			if (selection instanceof IStructuredSelection) {
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if (obj instanceof IServer) {
					selectedServer = (IServer)obj;
				}
				else if (obj instanceof ModuleServer) {
					ModuleServer ms = (ModuleServer)obj;
					selectedModule = ms.module[ms.module.length - 1];
					if (selectedModule != null)
						selectedServer = ms.server;
				}
			}
		}
	}
}
