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
package org.eclipse.wst.server.ui.internal.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorSite;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.IOrdered;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
/**
 *
 */
public interface IServerEditorActionFactory extends IOrdered {
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
	 * the result of the getFactoryIds() method.
	 *
	 * @return boolean
	 */
	public boolean supportsServerElementType(String id);

	/**
	 * Returns true if this editor page should be visible with the given server.
	 * This allows (for instance) complex configuration pages to only be shown when used
	 * with non-unittest servers.
	 */
	public boolean shouldDisplay(IServerWorkingCopy server);

	/**
	 * Create the action.
	 */
	public IAction createAction(IEditorSite site, IServerEditorPartInput input);
}