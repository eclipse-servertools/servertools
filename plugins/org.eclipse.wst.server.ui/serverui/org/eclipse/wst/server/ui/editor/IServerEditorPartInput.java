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
package org.eclipse.wst.server.ui.editor;

import org.eclipse.ui.IEditorInput;

import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 *
 *
 * @since 1.0
 */
public interface IServerEditorPartInput extends IEditorInput {
	/**
	 * Returns the server to be edited.
	 *
	 * @return a working copy of the server
	 */
	public IServerWorkingCopy getServer();

	/**
	 * Returns true if the server is read-only.
	 * 
	 * @return boolean <code>true</code> if the server is read-only,
	 *    and <code>false</code> otherwise
	 */
	public boolean isServerReadOnly();
	
	/**
	 * Gets the command manager. The editor is only responsible for creating an
	 * ICommand and passing it to the command manager, which actually performs
	 * the action and updates the server.
	 *
	 * @return commandManager the command manager
	 */
	public ICommandManager getServerCommandManager();
}