/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.editor;

import org.eclipse.ui.IEditorInput;

import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 * An input into a server part or section editor.
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
}