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
package org.eclipse.wst.server.ui.internal.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.ICommandManager;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
/**
 * 
 */
public class ServerEditorPartInput implements IServerEditorPartInput {
	protected IServerWorkingCopy server;
	protected boolean serverReadOnly;
	protected ICommandManager serverCommandManager;
	
	public ServerEditorPartInput(
			ICommandManager serverCommandManager, IServerWorkingCopy server,  boolean serverReadOnly) {
		
		this.server = server;
		this.serverReadOnly = serverReadOnly;
		this.serverCommandManager = serverCommandManager;
	}
	
	public String getName() {
		return "-";
	}

	public String getToolTipText() {
		return "-";
	}

	public boolean exists() {
		return true;
	}
	
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public ImageDescriptor getImageDescriptor() {
		return null;
	}
	
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * Returns the server to be edited.
	 *
	 * @return IServerResource
	 */
	public IServerWorkingCopy getServer() {
		return server;
	}

	/**
	 * Returns true if the server is read-only.
	 * 
	 * @return boolean
	 */
	public boolean isServerReadOnly() {
		return serverReadOnly;
	}
	
	public ICommandManager getServerCommandManager() {
		return serverCommandManager;
	}
	
	public String toString() {
		return "ServerEditorPartInput [" + server + "]";
	}
}