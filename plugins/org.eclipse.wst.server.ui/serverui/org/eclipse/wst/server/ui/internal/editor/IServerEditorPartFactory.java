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

import org.eclipse.ui.IEditorPart;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.IOrdered;
/**
 *
 */
public interface IServerEditorPartFactory extends IOrdered {
	/**
	 * 
	 */
	public String getId();
	
	/**
	 * 
	 */
	public String getName();

	/**
	 * Returns true if the given server resource type (given by the
	 * id) can be opened with this editor. This result is based on
	 * the result of the types attribute.
	 *
	 * @return boolean
	 */
	public boolean supportsType(String id);
	
	public boolean supportsInsertionId(String id);
	
	/**
	 * Returns true if this editor page should be visible with the given server.
	 * This allows (for instance) complex configuration pages to only be shown when used
	 * with non-unittest servers.
	 */
	public boolean shouldCreatePage(IServerWorkingCopy server);

	/**
	 * Create the editor page.
	 */
	public IEditorPart createPage();
}