package org.eclipse.wst.server.ui.internal.viewers;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
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
public class LockedCheckboxTableViewer extends CheckboxTableViewer {
	protected Color color;

	public LockedCheckboxTableViewer(Table table) {
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
			if (getLabelProvider() instanceof ILockedLabelProvider) {
				ILockedLabelProvider provider = (ILockedLabelProvider) getLabelProvider();
				if (provider.isLocked(element)) {
					item.setBackground(color);
					item.setImage(0, null);
				}
			}
		}
		super.doUpdateItem(widget, element, fullMap);
	}
}
