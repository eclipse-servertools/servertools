package org.eclipse.wst.server.ui.internal.view.servers;
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
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * Action for monitoring a server.
 */
public class MonitorServerAction extends Action {
	protected IServer server;
	protected Shell shell;

	/**
	 * MonitorServerAction constructor comment.
	 */
	public MonitorServerAction(Shell shell, IServer server) {
		super(ServerUIPlugin.getResource("%actionMonitorProperties"));
		this.shell = shell;
		this.server = server;
	}

	/**
	 * Invoked when an action occurs. 
	 */
	public void run() {
		MonitorServerDialog msd = new MonitorServerDialog(shell, server);
		msd.open();
	}
}