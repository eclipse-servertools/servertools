/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.audio;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
/**
 *
 */
public class CategoryTableViewer extends CheckboxTableViewer {
	protected Color color;

	public CategoryTableViewer(Table table) {
		super(table);
		createColor(table);
	}

	protected void createColor(Control c) {
		color = new Color(c.getDisplay(), 255, 255, 225);
		c.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				color.dispose();
			}
		});
	}

	public void doUpdateItem(Widget widget, Object element, boolean fullMap) {
		if (color == null)
			return;
		if (widget instanceof TableItem) {
			TableItem item = (TableItem) widget;
			if (element instanceof String) {
				item.setBackground(color);
			} else
				item.setBackground(null);
		}
		super.doUpdateItem(widget, element, fullMap);
	}
}