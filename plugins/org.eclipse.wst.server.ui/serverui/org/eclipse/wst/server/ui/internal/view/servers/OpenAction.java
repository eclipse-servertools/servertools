/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * "Open" menu action.
 */
public class OpenAction extends Action {
	protected IServer server;

	/**
	 * OpenAction constructor comment.
	 */
	public OpenAction(IServer server) {
		super(ServerUIPlugin.getResource("%actionOpen"));
	
		this.server = server;
		setEnabled(server.getServerType() != null);
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		try {
			ServerUIUtil.editServer(server);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error editing element", e);
		}
	}
}