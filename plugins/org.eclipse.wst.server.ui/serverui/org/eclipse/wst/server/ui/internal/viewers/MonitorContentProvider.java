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
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
/**
 * Monitor port content provider.
 */
public class MonitorContentProvider implements IStructuredContentProvider {
	protected IServer server;

	public MonitorContentProvider(IServer server) {
		super();
		this.server = server;
	}

	public void dispose() { }

	public Object[] getElements(Object inputElement) {
		return ServerCore.getServerMonitorManager().getMonitoredPorts(server).toArray();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}