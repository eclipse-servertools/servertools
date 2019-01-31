/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.wst.internet.monitor.ui.internal.view;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.wst.internet.monitor.core.internal.MonitorManager;
import org.eclipse.wst.internet.monitor.core.internal.http.ResendHTTPRequest;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
/**
 * Resends the selected request and adds the result to the request/response tree.
 */
public class ResendMessageAction implements IViewActionDelegate{
	protected ISelection selection = null;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (selection != null) {
			Object element = ((StructuredSelection) selection).getFirstElement();
			if (element != null && element instanceof Request) {
				Request req = (Request) element;
				ResendHTTPRequest newReq = MonitorManager.createResendRequest(req);
				MonitorManager.getInstance().addResendRequest(req, newReq);
				TreeViewer treeViewer = MonitorView.view.treeViewer;
				treeViewer.add(req, newReq);
				treeViewer.setSelection(new StructuredSelection(newReq), false);
				newReq.sendRequest();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection2) {
		this.selection = selection2;
	}
}