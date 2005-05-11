/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.view;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.wst.internet.monitor.core.internal.http.ResendHTTPRequest;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
import org.eclipse.wst.internet.monitor.ui.internal.provisional.ContentViewer;
import org.eclipse.wst.internet.monitor.ui.internal.viewers.HeaderViewer;
/**
 * Send a modified message.
 */
public class SendModifiedMessageAction implements IViewActionDelegate{
	ISelection selection = null;

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
			if (element != null && element instanceof ResendHTTPRequest) {
				ResendHTTPRequest req = (ResendHTTPRequest) element;
				ContentViewer curViewer = MonitorView.view.vm.getCurrentRequestViewer();
				HeaderViewer curHeaderViewer = MonitorView.view.vm.getCurrentRequestHeaderViewer();
				req.setRequest(curViewer.getContent(), Request.CONTENT);
				
				if (curHeaderViewer != null)
					req.setRequest(curHeaderViewer.getContent(), Request.TRANSPORT);

				req.sendRequest();
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