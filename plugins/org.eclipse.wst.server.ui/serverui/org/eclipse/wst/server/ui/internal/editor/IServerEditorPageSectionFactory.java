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

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.IOrdered;
import org.eclipse.wst.server.ui.editor.IServerEditorSection;
/**
 *
 */
public interface IServerEditorPageSectionFactory extends IOrdered {
	/**
	 * Returns the id.
	 * 
	 * @return the id
	 */
	public String getId();

	/**
	 * Returns true if the given server resource type (given by the
	 * id) can be opened with this editor. This result is based on
	 * the result of the types attribute.
	 *
	 * @param id the type id
	 * @return boolean
	 */
	public boolean supportsType(String id);
	
	public String getInsertionId();
	
	/**
	 * Returns true if this editor page section should be visible with the given server.
	 * This allows (for instance) complex configuration pages to only be shown when used
	 * with non-unittest servers.
	 * 
	 * @param server a server
	 * @return <code>true</code> if the section should be shown, and <code>false</code> otherwise
	 */
	public boolean shouldCreateSection(IServerWorkingCopy server);

	/**
	 * Create the editor page section.
	 * 
	 * @return the section
	 */
	public IServerEditorSection createSection();
}