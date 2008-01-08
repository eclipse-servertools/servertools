/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.custom;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
/**
 * This Layout stacks all the controls one on top of the other and resizes all controls
 * to have the same size and location.
 * The control specified in topControl is visible and all other controls are not visible.
 * Users must set the topControl value to flip between the visible items and then call 
 * layout() on the composite which has the MonitorStackLayout.
 */
public class MonitorStackLayout extends Layout {
 	/**
 	 * topControl the Control that is displayed at the top of the stack.
 	 * All other controls that are children of the parent composite will not be visible.
 	 */
 	public Control topControl;

	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		Control children[] = composite.getChildren();
		int maxWidth = 0;
		int maxHeight = 0;
		for (Control child : children) {
			if (child == topControl) {
				Point size = child.computeSize(wHint, hHint, flushCache);
				maxWidth = Math.max(size.x, maxWidth);
				maxHeight = Math.max(size.y, maxHeight);
			}
		}
		int width = maxWidth;
		int height = maxHeight;
		if (wHint != SWT.DEFAULT)
			width = wHint;
		if (hHint != SWT.DEFAULT)
			height = hHint;
		return new Point(width, height);
	}

	protected boolean flushCache(Control control) {
		return true;
	}

	protected void layout(Composite composite, boolean flushCache) {
		Control children[] = composite.getChildren();
		Rectangle rect = composite.getClientArea();
		for (Control child : children) {
			if (child instanceof Label) {
				Rectangle r = new Rectangle(rect.x+2, rect.y, rect.width-2, rect.height);
				child.setBounds(r);
			} else
				child.setBounds(rect);
			child.setVisible(child == topControl);
		}
	}

	public String toString() {
		return "MonitorStackLayout";
	}
}