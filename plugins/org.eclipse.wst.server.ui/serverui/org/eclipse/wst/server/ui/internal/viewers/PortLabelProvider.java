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
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerPort;
/**
 * Server port label provider.
 */
public class PortLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
	protected IServer server;

	public PortLabelProvider(IServer server) {
		super();
		this.server = server;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		IServerPort port = (IServerPort) element;
		if (columnIndex == 0)
			return notNull(port.getName());
		else if (columnIndex == 1)
			return port.getPort() + "";
		else
			return "";
	}
}