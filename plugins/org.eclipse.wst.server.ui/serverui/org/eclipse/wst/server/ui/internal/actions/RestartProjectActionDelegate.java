/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
/**
 * Action delegate for restarting a project within a running
 * server.
 */
public class RestartProjectActionDelegate implements IActionDelegate {
	protected IProject project;

	/**
	 * RestartProjectAction constructor comment.
	 */
	public RestartProjectActionDelegate() {
		super();
	}
	
	/**
	 * Performs this action.
	 * <p>
	 * This method is called when the delegating action has been triggered.
	 * Implement this method to do the actual work.
	 * </p>
	 *
	 * @param action the action proxy that handles the presentation portion of the
	 *   action
	 */
	public void run(IAction action) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Shell shell = EclipseUtil.getShell();
				MessageDialog dialog = new MessageDialog(shell, ServerUIPlugin.getResource("%defaultDialogTitle"), null, ServerUIPlugin.getResource("%dialogRestartingProject", project.getName()), MessageDialog.INFORMATION, new String[0], 0);
				dialog.setBlockOnOpen(false);
				dialog.open();
	
				IModule[] modules = ServerUtil.getModules(project);
				if (modules != null && modules.length > 0) {
					IModule module = modules[0];
					IServer[] servers = ServerUtil.getServersByModule(module, null);
					if (servers != null) {
						int size2 = servers.length;
						for (int j = 0; j < size2; j++) {
							int state = servers[j].getServerState();
							if (state == IServer.STATE_STARTED) {
								if (servers[j].canRestartModule(module)) {
									try {
										servers[j].restartModule(module, new NullProgressMonitor());
									} catch (Exception e) {
										Trace.trace(Trace.SEVERE, "Error restarting project", e);
									}
								}
							}
						}
					}
				}
				dialog.close();
			}
		});
	}
	
	/**
	 * Notifies this action delegate that the selection in the workbench has changed.
	 * <p>
	 * Implementers can use this opportunity to change the availability of the
	 * action or to modify other presentation properties.
	 * </p>
	 *
	 * @param action the action proxy that handles presentation portion of the action
	 * @param selection the current selection in the workbench
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		if (sel.isEmpty() || !(sel instanceof IStructuredSelection)) {
			action.setEnabled(false);
			return;
		}
	
		IStructuredSelection select = (IStructuredSelection) sel;
		Iterator iterator = select.iterator();
		Object selection = iterator.next();
		if (iterator.hasNext() || selection == null) {
			// more than one selection (should never happen)
			action.setEnabled(false);
			return;
		}
	
		if (!(selection instanceof IProject)) {
			action.setEnabled(false);
			return;
		}
	
		project = (IProject) selection;
		if (!project.isOpen()) {
			action.setEnabled(false);
			return;
		}
	
		IModule[] modules = ServerUtil.getModules(project);
		if (modules != null && modules.length > 0) {
			IModule module = modules[0];
			IServer[] servers = ServerUtil.getServersByModule(module, null);
			if (servers != null) {
				int size2 = servers.length;
				for (int j = 0; j < size2; j++) {
					int state = servers[j].getServerState();
					if (state == IServer.STATE_STARTED) {
						if (servers[j].canRestartModule(module)) {
							action.setEnabled(true);
							return;
						}
					}
				}
			}
		}

		action.setEnabled(false);
	}
}