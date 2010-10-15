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
import org.eclipse.wst.server.core.ServerPort;
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
		ServerPort port = (ServerPort) element;
		if (columnIndex == 0)
			return notNull(port.getName());
		else if (columnIndex == 1)
			return port.getPort() + "";
		else
			return "";
	}
}