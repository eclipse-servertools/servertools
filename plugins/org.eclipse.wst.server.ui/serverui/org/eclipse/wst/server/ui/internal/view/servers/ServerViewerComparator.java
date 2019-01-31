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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class ServerViewerComparator extends ViewerComparator {
	public static final int MAX_DEPTH = 3;
	public static final int ASCENDING = 1;
	public static final int DESCENDING = -1;

	protected ServerTableLabelProvider labelProvider;

	protected int[] priorities = new int[] { 0 };

	protected int[] directions = new int[] { ASCENDING };

	public ServerViewerComparator(ServerTableLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	public void setTopPriority(int priority) {
		if (priorities[0] == priority)
			return;
		
		int len = priorities.length + 1;
		if (len > MAX_DEPTH)
			len = MAX_DEPTH;
		
		int[] temp = new int[len];
		System.arraycopy(priorities, 0, temp, 1, len - 1);
		temp[0] = priority;
		priorities = temp;
		
		temp = new int[len];
		System.arraycopy(directions, 0, temp, 1, len - 1);
		temp[0] = ASCENDING;
		directions = temp;
	}

	public int getTopPriority() {
		return priorities[0];
	}

	public void setTopPriorityDirection(int direction) {
		if (direction == ASCENDING || direction == DESCENDING) {
			directions[0] = direction;
		}
	}

	public int getTopPriorityDirection() {
		return directions[0];
	}

	public void reverseTopPriority() {
		directions[0] *= -1;
	}

	public int compare(Viewer viewer, Object e1, Object e2, int a) {
		int col = priorities[a];
		
		String s1 = labelProvider.getColumnText(e1, col);
		String s2 = labelProvider.getColumnText(e2, col);
		
		int s = s1.compareToIgnoreCase(s2) * directions[a];
		if (s == 0) {
			if (a == priorities.length - 1)
				return 0;
			return compare(viewer, e1, e2, a+1);
		}
		return s;
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		return compare(viewer, e1, e2, 0);
	}
}