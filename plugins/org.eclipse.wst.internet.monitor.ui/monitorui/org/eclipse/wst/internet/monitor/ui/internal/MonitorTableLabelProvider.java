/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Tianchao Li (Tianchao.Li@gmail.com) - Start monitors by default 
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal;

import org.eclipse.jface.viewers.ILabelProviderListener;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitor;
import org.eclipse.jface.viewers.ITableLabelProvider;
/**
 * Monitor table label provider.
 */
public class MonitorTableLabelProvider implements ITableLabelProvider {
	/**
	 * MonitorTableLabelProvider constructor comment.
	 */
	public MonitorTableLabelProvider() {
		super();
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * @see ITableLabelProvider#getColumnImage(Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			IMonitor monitor = (IMonitor) element;
			if (monitor.isRunning())
				return MonitorUIPlugin.getImage(MonitorUIPlugin.IMG_MONITOR_ON);
			return MonitorUIPlugin.getImage(MonitorUIPlugin.IMG_MONITOR_OFF);
		}
		return null;
	}

	/**
	 * @see ITableLabelProvider#getColumnText(Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		IMonitor monitor = (IMonitor) element;
		if (columnIndex == 0) {
			if (monitor.isRunning())
				return Messages.started;
			return Messages.stopped;
		} else if (columnIndex == 1)
			return monitor.getRemoteHost() + ":" + monitor.getRemotePort();
		else if (columnIndex == 2)
			return monitor.getProtocol();
		else if (columnIndex == 3)
			return monitor.getLocalPort() + "";
		else if (columnIndex == 4) {
			if (monitor.isAutoStart())
				return Messages.yes;
			return Messages.no;
		} else
			return "X";
	}

	protected String notNull(String s) {
		if (s == null)
			return "";
		
		return s;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}
}