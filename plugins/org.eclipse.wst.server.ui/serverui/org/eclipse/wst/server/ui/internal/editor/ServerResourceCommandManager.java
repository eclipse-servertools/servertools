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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.editor.ICommandManager;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * A command manager for a single server resource.
 */
public class ServerResourceCommandManager implements ICommandManager {
	protected ServerEditor editor;
	protected GlobalCommandManager commandManager;
	protected String id;

	public ServerResourceCommandManager(ServerEditor editor, String id, GlobalCommandManager commandManager) {
		this.editor = editor;
		this.commandManager = commandManager;
		this.id = id;
	}
	
	public boolean isReadOnly() {
		return commandManager.isReadOnly(id);
	}

	/**
	 * Execute the given command and place it in the undo stack.
	 * If the command cannot be undone, the user will be notifed
	 * before it is executed.
	 *
	 * @param command ICommand
	 */
	public void executeCommand(ITask command) {
		if (!validateEdit())
			return;

		if (commandManager.isReadOnly(id)) {
			warnReadOnly();
			return;
		}
		commandManager.executeCommand(id, command);
	}

	protected void warnReadOnly() {
		String title = ServerUIPlugin.getResource("%editorResourceWarnTitle");
		String message = ServerUIPlugin.getResource("%editorResourceWarnMessage");
		
		MessageDialog.openWarning(editor.getEditorSite().getShell(), title, message);
	}

	/**
	 * 
	 */
	protected boolean validateEdit() {
		if (commandManager.isDirty(id))
			return true;

		IFile[] files = commandManager.getReadOnlyFiles(id);
		if (files.length == 0)
			return true;
		
		IStatus status = ResourcesPlugin.getWorkspace().validateEdit(files, editor.getEditorSite().getShell());
		
		if (status.getSeverity() == IStatus.ERROR) {
			// inform user
			String message = ServerUIPlugin.getResource("%editorValidateEditFailureMessage");
			ErrorDialog.openError(editor.getEditorSite().getShell(), ServerUIPlugin.getResource("%errorDialogTitle"), message, status);
			
			// change to read-only
			commandManager.setReadOnly(id, true);
			
			// do not execute command
			return false;
		}
		// check file timestamp
		IServerAttributes serverfile = commandManager.getServerResource(id);
		if (commandManager.hasChanged(id)) {
			if (serverfile instanceof IServer)
				editor.promptReloadServerFile(id, (IServerWorkingCopy) serverfile);
		}
		
		// allow edit
		return true;
	}
}