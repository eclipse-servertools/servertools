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

import org.eclipse.ui.IEditorPart;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.IOrdered;
/**
 *
 */
public interface IServerEditorPartFactory extends IOrdered {
	/**
	 * Returns the id.
	 * 
	 * @return the id
	 */
	public String getId();
	
	/**
	 * Return the displayable name.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Returns true if the given server resource type (given by the
	 * id) can be opened with this editor. This result is based on
	 * the result of the types attribute.
	 * 
	 * @param id a server type id
	 * @return <code>true</code> if the type is supported
	 */
	public boolean supportsType(String id);

	/**
	 * Returns true if a given insertion id is supported.
	 * 
	 * @param id
	 * @return <code>true</code> if the insertion id is supported
	 */
	public boolean supportsInsertionId(String id);
	
	/**
	 * Returns true if this editor page should be visible with the given server.
	 * This allows (for instance) complex configuration pages to only be shown when used
	 * with non-unittest servers.
	 * 
	 * @param server a server
	 * @return <code>true</code> if the page should be visible
	 */
	public boolean shouldCreatePage(IServerWorkingCopy server);

	/**
	 * Create the editor page.
	 * 
	 * @return the editor page
	 */
	public IEditorPart createPage();
}