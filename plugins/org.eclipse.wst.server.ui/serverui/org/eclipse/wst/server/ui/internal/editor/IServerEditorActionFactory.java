/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
import org.eclipse.ui.IEditorSite;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
/**
 *
 */
public interface IServerEditorActionFactory extends IOrdered {
	/**
	 * Returns the id.
	 * 
	 * @return an id
	 */
	public String getId();

	/**
	 * Returns the name.
	 * 
	 * @return a name
	 */
	public String getName();

	/**
	 * Returns true if the given server resource type (given by the
	 * id) can be opened with this editor. This result is based on
	 * the result of the getFactoryIds() method.
	 *
	 * @param id an id
	 * @return boolean
	 */
	public boolean supportsServerElementType(String id);

	/**
	 * Returns true if this editor page should be visible with the given server.
	 * This allows (for instance) complex configuration pages to only be shown when used
	 * with non-unittest servers.
	 * 
	 * @param server a server
	 * @return <code>true</code> if the action should display
	 */
	public boolean shouldDisplay(IServerWorkingCopy server);

	/**
	 * Create the action.
	 * 
	 * @param site an editor site
	 * @param input a server editor input
	 * @return an action
	 */
	public IAction createAction(IEditorSite site, IServerEditorPartInput input);
}