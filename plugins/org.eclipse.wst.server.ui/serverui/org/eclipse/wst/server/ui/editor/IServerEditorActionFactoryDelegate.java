package org.eclipse.wst.server.ui.editor;
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

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorSite;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
/**
 *
 */
public interface IServerEditorActionFactoryDelegate {
	/**
	 * Returns true if this editor action should be visible with the given
	 * server and configuration combination. This allows (for
	 * instance) complex configuration pages to only be shown when used
	 * with non-unittest servers.
	 *
	 * <p>If the server or configuration is being opened by itself, the
	 * other value (server or configuration) will be null.
	 */
	public boolean shouldDisplay(IServer server, IServerConfiguration configuration);

	/**
	 * Create the action.
	 */
	public IAction createAction(IEditorSite site, IServerEditorPartInput input);
}
