/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
/**
 * 
 */
public interface IServerEditorSection {
	public void init(IEditorSite site, IEditorInput input);
	
	public void createSection(Composite parent);
	
	public void dispose();
	
	/**
	 * Return the error message for this page.
	 * 
	 * @return java.lang.String
	 */
	public String getErrorMessage();

	/**
	 * Returns error or status messages that will be displayed when the
	 * server resource is saved. If there are any error messages, the
	 * user will be unable to save the editor.
	 * 
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus[] getSaveStatus();
}