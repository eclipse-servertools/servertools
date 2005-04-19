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
package org.eclipse.wst.server.ui.internal.provisional;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorSite;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
/**
 *
 * @since 1.0
 */
public abstract class ServerEditorActionFactoryDelegate {
	/**
	 * Returns true if this editor action should be visible on the given
	 * server. This allows actions to be filtered based on the server type
	 * or server attributes.
	 * 
	 * @param server the server being edited
	 * @return <code>true</code> if the action should be displayed, or
	 *    <code>false</code> otherwise
	 */
	public boolean shouldDisplay(IServerWorkingCopy server) {
		return true;
	}

	/**
	 * Create the action.
	 * 
	 * @param site the editor site
	 * @param input the server editor port input
	 * @return an action that can be run against the server
	 */
	public abstract IAction createAction(IEditorSite site, IServerEditorPartInput input);
}