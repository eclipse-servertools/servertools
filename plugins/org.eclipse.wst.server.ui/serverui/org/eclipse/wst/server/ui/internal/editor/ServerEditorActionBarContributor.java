/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;
/**
 * Server editor action bar contributor.
 */
public class ServerEditorActionBarContributor extends EditorActionBarContributor {
	public static final String SERVER_EDITOR_SEPARATOR = "server-editor-additions";

	// current editor
	protected ServerEditor editor;

	/**
	 * ServerEditorActionBarContributor constructor comment.
	 */
	public ServerEditorActionBarContributor() {
		super();
	}

	/**
	 * Sets the active editor for the contributor.
	 * <p>
	 * The <code>EditorActionBarContributor</code> implementation of this method does
	 * nothing. Subclasses may reimplement. This generally entails disconnecting
	 * from the old editor, connecting to the new editor, and updating the actions
	 * to reflect the new editor.
	 * </p>
	 * 
	 * @param targetEditor the new target editor
	 */
	public void setActiveEditor(IEditorPart targetEditor) {
		super.setActiveEditor(targetEditor);

		if (targetEditor != null && targetEditor.equals(editor))
			return;
		
		IActionBars actionBars = getActionBars();
		boolean actionBarsUpdated = false;

		if (editor != null) {
			editor.setStatus(null, null);
			
			IStatusLineManager status = actionBars.getStatusLineManager();
			status.removeAll();

			IToolBarManager tbm = actionBars.getToolBarManager();
			tbm.removeAll();
			
			actionBarsUpdated = true;
		}

		if (targetEditor instanceof ServerEditor) {
			editor = (ServerEditor) targetEditor;
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Editor action bar contributor for: " + editor);
			}
			editor.updateUndoAction();
			editor.updateRedoAction();
			
			actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), editor.getUndoAction());
			actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), editor.getRedoAction());
			
			actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), editor.getCopyAction());
			actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), editor.getCutAction());
			actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), editor.getPasteAction());
			
			IStatusLineManager status = actionBars.getStatusLineManager();
			StatusLineContributionItem item = new StatusLineContributionItem("id");
			status.add(item);

			editor.setStatus(status, item);
			editor.updateStatusLine();

			IAction[] actions = editor.getEditorActions();
			IToolBarManager tbm = actionBars.getToolBarManager();
			tbm.add(new Separator(SERVER_EDITOR_SEPARATOR));
			boolean modified = false;
			if (actions != null) {
				int size = actions.length;
				if (Trace.FINEST) {
					Trace.trace(Trace.STRING_FINEST, "Attempting to add editor actions: " + size);
				}
				for (int i = 0; i < size; i++) {
					if (Trace.FINEST) {
						Trace.trace(Trace.STRING_FINEST, "action: " + actions[i]);
					}
					tbm.appendToGroup(SERVER_EDITOR_SEPARATOR, actions[i]);
					modified = true;
				}
			}
			
			if (modified)
				tbm.update(false);
			actionBarsUpdated = true;
		} else
			editor = null;
		
		if (actionBarsUpdated)
			actionBars.updateActionBars();
	}
}