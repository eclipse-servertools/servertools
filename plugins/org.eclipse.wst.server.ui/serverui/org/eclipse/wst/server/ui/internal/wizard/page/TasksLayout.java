/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
		return new Point(wHint, hHint);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite, boolean)
	 */
	protected void layout(Composite composite, boolean flushCache) {
		Control[] children = composite.getChildren();
		Rectangle r = composite.getClientArea();
		
		int y = r.y;
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				if (i % 2 == 0) {
					int h = children[i].computeSize(r.width, SWT.DEFAULT).y;
					children[i].setBounds(r.x, y, r.width, h);
					y += h + verticalSpacing;
				} else {
					int h = Math.max(50, children[i].computeSize(r.width - 20, SWT.DEFAULT).y);
					children[i].setBounds(r.x + 20, y, r.width - 20, h);
					y += h + verticalSpacing;
				}
			}
		}
	}
}