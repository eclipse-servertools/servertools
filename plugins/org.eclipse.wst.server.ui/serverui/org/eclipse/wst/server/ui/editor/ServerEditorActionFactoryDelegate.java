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
package org.eclipse.wst.server.ui.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorSite;

import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 *
 */
public abstract class ServerEditorActionFactoryDelegate {
	/**
	 * Returns true if this editor action should be visible on the given
	 * server. This allows actions to be filtered based on the server type
	 * or server attributes.
	 * 
	 * @param server
	 */
	public boolean shouldDisplay(IServerWorkingCopy server) {
		return true;
	}

	/**
	 * Create the action.
	 */
	public abstract IAction createAction(IEditorSite site, IServerEditorPartInput input);
}