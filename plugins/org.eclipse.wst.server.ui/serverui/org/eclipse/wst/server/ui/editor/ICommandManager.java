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

import org.eclipse.wst.server.core.ITask;
/**
 * A command manager.
 */
public interface ICommandManager {
	/**
	 * Execute the given command and place it in the undo stack.
	 * If the command cannot be undone, the user will be notifed
	 * before it is executed.
	 *
	 * @param task
	 */
	public void executeCommand(ITask task);
}