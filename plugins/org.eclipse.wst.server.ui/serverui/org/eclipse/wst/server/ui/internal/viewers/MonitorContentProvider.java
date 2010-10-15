/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.ServerMonitorManager;
/**
 * Monitor port content provider.
 */
public class MonitorContentProvider extends BaseContentProvider {
	protected IServer server;

	public MonitorContentProvider(IServer server) {
		super();
		this.server = server;
	}

	public Object[] getElements(Object inputElement) {
		return ServerMonitorManager.getInstance().getMonitoredPorts(server);
	}
}