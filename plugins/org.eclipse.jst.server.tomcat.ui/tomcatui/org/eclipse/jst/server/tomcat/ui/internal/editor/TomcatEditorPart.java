package org.eclipse.jst.server.tomcat.ui.internal.editor;
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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.ui.editor.ServerResourceEditorPart;
/**
 *
 */
public abstract class TomcatEditorPart extends ServerResourceEditorPart {
	/**
	 * @see IEditorPart#doSave(IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) { }

	/**
	 * @see IEditorPart#doSaveAs()
	 */
	public void doSaveAs() { }

	/**
	 * @see IEditorPart#gotoMarker(IMarker)
	 */
	public void gotoMarker(IMarker marker) { }

	/**
	 * @see IEditorPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/**
	 * @see IEditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}
}