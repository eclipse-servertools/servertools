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

	/**
	 * Creates an <code>IElement</code> from the state captured within 
	 * an <code>IMemento</code>.
	 *
	 * @param memento a memento containing the state for an element
	 * @return an element, or <code>null</code> if the element could not be created
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
	 */
	public static void saveState(IMemento memento, ServerEditorInput input) {
		if (input == null)
			return;
			
		if (input.getServerId() != null)
			memento.putString(SERVER_ID, input.getServerId());
	}
}