package org.eclipse.wst.server.ui.actions;
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
import org.eclipse.swt.widgets.Shell;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
/**
 * 
 */
public interface IServerAction {
	/**
	 * Performs this action.
	 * 
	 */
	public void run(Shell shell, IServer server, IServerConfiguration configuration);

	/**
	 * 
	 */
	public boolean supports(IServer server, IServerConfiguration configuration);
}
