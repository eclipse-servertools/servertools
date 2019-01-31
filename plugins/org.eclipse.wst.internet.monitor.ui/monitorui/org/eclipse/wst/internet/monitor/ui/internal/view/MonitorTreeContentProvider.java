/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.internet.monitor.core.internal.MonitorManager;
import org.eclipse.wst.internet.monitor.core.internal.http.ResendHTTPRequest;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
/**
 * Content provider for the monitor server view.
 */
public class MonitorTreeContentProvider implements ITreeContentProvider {
	protected static final String ROOT = "root";

	/**
	 * ProxyTreeContentProvider constructor comment.
	 */
	public MonitorTreeContentProvider() {
		super();
	}

	/**
	 * Disposes of this content provider.
	 * <p>
	 * [Issue: This method should be changed to take a Viewer,
	 * renamed and repurposed to disconnect a content provider from
	 * a viewer. This is over and above what inputChanged does,
	 * which is disconnecting a content provider from the viewer's
	 * input (but not the viewer itself).
	 * ]
	 * </p>
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * Returns an iterator over the child elements of the given element.
	 * <p>
	 * Note: The difference between this method and 
	 * <code>IStructuredContentProvider.getElements</code> is
	 * that <code>getElements</code> is called to obtain the 
	 * tree viewer's root elements, whereas <code>getChildren</code> is used
	 * to obtain the children of a given node in the tree
	 * (including a root).
	 * </p>
	 * <p>
	 * [Issue: Don't know what above is trying to say.
	 *  See IStructuredContentProvider.getElements.
	 * ]
	 * </p>
	 *
	 * @param element the element
	 * @return an iterator over the child elements 
	 *    (element type: <code>Object</code>)
	 */
	public Object[] getChildren(Object element) {
		if (element instanceof Integer) {
			Integer in = (Integer) element;
			List<Request> list = new ArrayList<Request>();
			Request[] requests = MonitorUIPlugin.getInstance().getRequests();
			if (requests != null) {
				for (Request req : requests) {
					if ((req.getLocalPort() == in.intValue())
							&& !(req instanceof ResendHTTPRequest))
						list.add(req);
				}
			}
			return list.toArray();
		} else if (element instanceof Request) {
			Request req = (Request) element;
			ResendHTTPRequest[] rr = MonitorManager.getInstance().getResendRequests(req);
			List<Request> list = new ArrayList<Request>();
			if (rr != null) {
				for (ResendHTTPRequest r : rr)
					list.add(r);
			}
			return list.toArray();
		}
		return null;
	}

	/*
	 * Returns an iterator over the elements belonging to the
	 * given element. These elements can be presented as rows in a table,
	 * items in a list, etc.
	 */
	public Object[] getElements(Object element) {
		if (ROOT.equals(element)) {
			List<Integer> list = new ArrayList<Integer>();
			Request[] requests = MonitorUIPlugin.getInstance().getRequests();
			if (requests != null) {
				for (Request req :  requests) {
					Integer in = new Integer(req.getLocalPort());
					if (!list.contains(in))
						list.add(in);
				}
			}
			return list.toArray();
		}
		return getChildren(element);
	}

	/*
	 * Returns the parent for the given element, or <code>null</code> 
	 * indicating that the parent can't be computed. 
	 */
	public Object getParent(Object element) {
		if (element != null) {
			if (element instanceof Integer)
				return ROOT;
			Request call = (Request) element;
			if (call instanceof ResendHTTPRequest) {
				ResendHTTPRequest callResend = (ResendHTTPRequest) call;
				Request parent = callResend.getOriginalRequest();
				if (parent != null)
					return parent;
			}
			return new Integer(call.getLocalPort());
		}
		return null;
	}

	/*
	 * Returns whether the given element has children.
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof Integer)
			return true;
		if (element instanceof Request)
			return MonitorManager.getInstance().getResendRequests((Request) element).length > 0; 
		
		return false;
	}

	/*
	 * Notifies this content provider that the given viewer's input
	 * has been switched to a different element.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
}