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
package org.eclipse.wst.server.ui.internal;

import java.util.List;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
/**
 *
 */
public class ServerLaunchShortcut implements ILaunchShortcut {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers.ISelection, java.lang.String)
	 */
	public void launch(ISelection selection, final String mode) {
		RunOnServerActionDelegate del = new RunOnServerActionDelegate() {
			protected String getLaunchMode() {
				return mode;
			}
		};
		IAction action = new Action() {
			// dummy action
		};
		del.selectionChanged(action, selection);
		del.run(action);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart, java.lang.String)
	 */
	public void launch(IEditorPart editor, String mode) {
		if (editor == null)
			return;
		
		// check if the editor input itself can be run. Otherwise, check if
		// the editor has a file input that can be run
		IEditorInput input = editor.getEditorInput();
		List list = ServerUtil.getModuleObjects(input);
		if (!list.isEmpty()) {
			launch(new StructuredSelection(input), mode);
		} else if (input instanceof IFileEditorInput) {
			IFileEditorInput fei = (IFileEditorInput) input;
			ISelection sel = new StructuredSelection(fei.getFile());
			launch(sel, mode);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchFilter#testAttribute(org.eclipse.core.resources.IResource, java.lang.String, java.lang.String)
	 */
	/*public boolean testAttribute(IResource target, String name, String value) {
		return true;
	}*/
}