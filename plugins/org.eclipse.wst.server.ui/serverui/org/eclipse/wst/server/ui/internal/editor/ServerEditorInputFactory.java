/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
/**
 * This factory is used in the persistence of ServerResourceEditorInput
 * instances. This allows the user to close the workbench with an
 * open editor and reopen to the same editor.
 */
public class ServerEditorInputFactory implements IElementFactory {
	protected final static String FACTORY_ID = "org.eclipse.wst.server.ui.editor.input.factory";
	protected final static String SERVER_ID = "server-id";

	/**
	 * ServerEditorInputFactory constructor comment.
	 */
	public ServerEditorInputFactory() {
		// do nothing
	}

	/*
	 * Creates editor input based on the state in the memento.
	 */
	public IAdaptable createElement(IMemento memento) {
		// get the resource names
		String serverId = memento.getString(SERVER_ID);
		
		return new ServerEditorInput(serverId);
	}

	/**
	 * Saves the state of an element within a memento.
	 *
	 * @param memento the storage area for element state
	 * @param input server editor input
	 */
	public static void saveState(IMemento memento, ServerEditorInput input) {
		if (input == null)
			return;
			
		if (input.getServerId() != null)
			memento.putString(SERVER_ID, input.getServerId());
	}
}