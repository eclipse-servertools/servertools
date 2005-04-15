/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * "Open" menu action.
 */
public class OpenAction extends Action {
	protected IServer server;

	/**
	 * OpenAction constructor.
	 * 
	 * @param server a server
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
			ServerUIPlugin.editServer(server);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error editing element", e);
		}
	}
}