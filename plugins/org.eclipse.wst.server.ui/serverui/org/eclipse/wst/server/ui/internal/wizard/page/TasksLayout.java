/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
/**
 * 
 */
public class TasksLayout extends Layout {
	private int verticalSpacing;

	public TasksLayout(int verticalSpacing) {
		this.verticalSpacing = verticalSpacing;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Layout#computeSize(org.eclipse.swt.widgets.Composite, int, int, boolean)
	 */
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		//return new Point(wHint, hHint);
		Control[] children = composite.getChildren();
		
		int y = 5;
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				if (i % 2 == 0) {
					int h = children[i].computeSize(wHint, SWT.DEFAULT).y;
					y += h + verticalSpacing / 2;
				} else {
					int h = Math.max(45, children[i].computeSize(wHint, SWT.DEFAULT).y);
					y += h + verticalSpacing;
				}
			}
		}
		return new Point(200, y); // + verticalSpacing);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite, boolean)
	 */
	protected void layout(Composite composite, boolean flushCache) {
		Control[] children = composite.getChildren();
		Rectangle r = composite.getClientArea();
		
		int y = r.y + 5;
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				if (i % 2 == 0) {
					int h = children[i].computeSize(r.width - 10, SWT.DEFAULT).y;
					children[i].setBounds(r.x + 5, y, r.width - 10, h);
					y += h + verticalSpacing / 2;
				} else {
					int h = Math.max(45, children[i].computeSize(r.width - 25, SWT.DEFAULT).y);
					children[i].setBounds(r.x + 20, y, r.width - 25, h);
					y += h + verticalSpacing;
				}
			}
		}
	}
}