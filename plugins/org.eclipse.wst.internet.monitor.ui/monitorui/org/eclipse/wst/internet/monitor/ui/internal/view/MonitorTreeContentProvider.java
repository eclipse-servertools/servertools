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
package org.eclipse.wst.internet.monitor.ui.internal.view;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.internet.monitor.core.IRequest;
import org.eclipse.wst.internet.monitor.core.IResendRequest;
import org.eclipse.wst.internet.monitor.core.MonitorCore;
/**
 * Content provider for the monitor server view.
 */
public class MonitorTreeContentProvider implements ITreeContentProvider{
	protected static final String ROOT = "root";

	protected boolean sortByResponseTime;

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
			List list = new ArrayList();
			IRequest[] requests = MonitorCore.getRequests();
			if (requests != null) {
				int size = requests.length;
				for (int i = 0; i < size; i++) {
					IRequest req = requests[i];
					if ((req.getLocalPort() == in.intValue())
							&& !(req instanceof IResendRequest))
						list.add(req);
				}
			}
			if (sortByResponseTime)
				sortByResponseTime(list);
			return list.toArray();
		} else if (element instanceof IRequest) {
			IRequest req = (IRequest) element;
			IResendRequest[] rr = req.getResendRequests();
			List list = new ArrayList();
			if (rr != null) {
				int size = rr.length;
				for (int i = 0; i < size; i++) {
					list.add(rr[i]);
				}
			}
			if (sortByResponseTime)
				sortByResponseTime(list);
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
			List list = new ArrayList();
			IRequest[] requests = MonitorCore.getRequests();
			if (requests != null) {
				int size = requests.length;
				for (int i = 0; i < size; i++) {
					IRequest req = requests[i];
					Integer in = new Integer(req.getLocalPort());
					if (!list.contains(in))
						list.add(in);
				}
			}
			if (sortByResponseTime)
				sortByResponseTime(list);
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
			IRequest call = (IRequest) element;
			if (call instanceof IResendRequest) {
				IResendRequest callResend = (IResendRequest) call;
				IRequest parent = callResend.getOriginalRequest();
				if (parent != null)
					return parent;
			}
			return new Integer(call.getLocalPort());
		}
		return null;
	}

	/*
	 * Returns true if the elements are currently being sorted by response time.
	 */
	public boolean getSortByResponseTime() {
		return sortByResponseTime;
	}

	/*
	 * Returns whether the given element has children.
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof Integer)
			return true;
		if (element instanceof IRequest) {
			return ((IRequest) element).getResendRequests().length > 0;
		}
		return false;
	}

	/*
	 * Notifies this content provider that the given viewer's input
	 * has been switched to a different element.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	/**
	 * Sets the sort by response time option.
	 * @param b boolean
	 */
	public void setSortByResponseTime(boolean b) {
		sortByResponseTime = b;
	}

	/**
	 * 
	 */
	protected void sortByResponseTime(List list) {
		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				IRequest c1 = (IRequest) list.get(i);
				IRequest c2 = (IRequest) list.get(j);
				if (c1.getResponseTime() < c2.getResponseTime()) {
					list.set(i, c2);
					list.set(j, c1);
				}
			}
		}
	}
}