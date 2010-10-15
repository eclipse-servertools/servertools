/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.IMonitoredServerPort;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Monitor port label provider.
 */
public class MonitorLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
	protected IServer server;

	public MonitorLabelProvider(IServer server) {
		super();
		this.server = server;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		IMonitoredServerPort port = (IMonitoredServerPort) element;
		if (columnIndex == 0) {
			if (port.isStarted())
				return ImageResource.getImage(ImageResource.IMG_MONITOR_ON);
			return ImageResource.getImage(ImageResource.IMG_MONITOR_OFF);
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		IMonitoredServerPort port = (IMonitoredServerPort) element;
		if (columnIndex == 0) {
			if (port.isStarted())
				return Messages.started;
			return Messages.stopped;
		} else if (columnIndex == 1)
			return notNull(port.getServerPort().getName());
		else if (columnIndex == 2)
			return port.getServerPort().getPort() + "";
		else if (columnIndex == 3)
			return port.getMonitorPort() + "";
		else {
			String[] content = port.getContentTypes();
			if (content == null || content.length == 0)
				return Messages.dialogMonitorContentTypeAll;
			
			StringBuffer sb = new StringBuffer();
			int size = content.length;
			for (int i = 0; i < size; i++) {
				if (i > 0)
					sb.append(",");
				sb.append(getContentTypeString(content[i]));
			}
			return sb.toString();
		}
	}
	
	protected static String getContentTypeString(String s) {
		if ("web".equals(s))
			return Messages.dialogMonitorContentTypeWeb;
		else if ("webservices".equals(s))
			return Messages.dialogMonitorContentTypeWebServices;
		else
			return s;
	}
}