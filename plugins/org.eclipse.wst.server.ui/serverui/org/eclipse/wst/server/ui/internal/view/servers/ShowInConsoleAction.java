/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
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

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.*;
import org.eclipse.ui.console.*;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * "Show in Console" menu action.
 */
public class ShowInConsoleAction extends AbstractServerAction {
	/**
	 * ShowInConsoleAction constructor.
	 * 
	 * @param sp a selection provider
	 */
	public ShowInConsoleAction(ISelectionProvider sp) {
		super(sp, "Console");
		
		IViewRegistry reg = PlatformUI.getWorkbench().getViewRegistry();
		IViewDescriptor desc = reg.find(IConsoleConstants.ID_CONSOLE_VIEW);
		setText(desc.getLabel());
		setImageDescriptor(desc.getImageDescriptor());
	}

	public boolean accept(IServer server) {
		return (server.getServerType() != null 
				&& server.getServerState() != IServer.STATE_STOPPED
				&& server.getLaunch() != null 
				&& server.getLaunch().getProcesses() != null
				&& server.getLaunch().getProcesses().length >= 1);
	}

	public void perform(IServer server) {
		try {
			ILaunch launch = server.getLaunch();
			selectProcess(launch.getProcesses()[0]);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error opening console", e);
			}
		}
	}

	protected void selectProcess(IProcess process) {
		// see bug 250999 - debug UI must be loaded before looking for debug consoles
		org.eclipse.debug.ui.console.IConsole.class.toString();
		
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		IConsole[] consoles = consoleManager.getConsoles();
		int size = consoles.length;
		IConsole console = null;
		for (int i = 0; i < size; i++) {
			if (consoles[i] instanceof org.eclipse.debug.ui.console.IConsole) {
				org.eclipse.debug.ui.console.IConsole con = (org.eclipse.debug.ui.console.IConsole) consoles[i];
				if (process.equals(con.getProcess()))
					console = consoles[i];
			}
		}
		
		if (console == null)
			return;
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow() ;
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IWorkbenchPart part = page.findView(IConsoleConstants.ID_CONSOLE_VIEW);
				if (part == null) {
					try {
						part = page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
					} catch (PartInitException e) {
						if (Trace.SEVERE) {
							Trace.trace(Trace.STRING_SEVERE, "Could not open console view");
						}
					}
				}
				if (part != null) {
					page.activate(part);
					IConsoleView view = (IConsoleView) part.getAdapter(IConsoleView.class);
					if (view != null) {
						view.setFocus();
						view.display(console);
					}
				}
			}
		}
	}
}