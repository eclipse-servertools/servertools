/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
/**
 * Server action handler.
 */
public class ServerActionHandler extends AbstractHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);
		Object obj = null;
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection select = (IStructuredSelection) sel;
			obj = select.getFirstElement();
		}
		
		String id = event.getCommand().getId();
		if (id.endsWith("publish")) {
			if (obj instanceof IServer) {
				PublishAction.publish((IServer) obj, HandlerUtil.getActiveShell(event));
			} else
				throw new ExecutionException("No server selected");
			return null;
		}
		
		String mode = ILaunchManager.RUN_MODE;
		if (id.endsWith("debug"))
			mode = ILaunchManager.DEBUG_MODE;
		else if (id.endsWith("profile"))
			mode = ILaunchManager.PROFILE_MODE;
		else if (id.endsWith("stop"))
			mode = null;
		
		if (obj instanceof IServer) {
			IServer server = (IServer) obj;
			if (mode == null)
				StopAction.stop(server, HandlerUtil.getActiveShell(event));
			else
				StartAction.start(server, mode, HandlerUtil.getActiveShell(event));
			return null;
		}
		
		RunOnServerActionDelegate ros = new RunOnServerActionDelegate();
		ros.setLaunchMode(mode);
		IAction action = new Action() {
			// dummy action
		};
		ros.selectionChanged(action, sel);
		ros.run(action);
		
		return null;
	}
}