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
package org.eclipse.wst.internet.monitor.ui.internal.view;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;

public class RequestComparator extends ViewerComparator {
	/*
	 * @see ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object o1, Object o2) {
		if (o1 instanceof Request && o2 instanceof Request) {
			Request r1 = (Request) o1;
			Request r2 = (Request) o2;
			if (r1.getResponseTime() < r2.getResponseTime())
				return -1;
			return 1;
		}
		return super.compare(viewer, o1, o2);
	}
}