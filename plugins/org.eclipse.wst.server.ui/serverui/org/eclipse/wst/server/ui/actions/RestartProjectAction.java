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
package org.eclipse.wst.server.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * Action to restart an individual project on servers that
 * support it.
 * 
 * @deprecated too many modules could be in a single project - it no
 *    longer makes sense to restart them all on every server they are on.
 * @since 1.0
 */
public class RestartProjectAction extends Action {
	/**
	 * RestartProjectAction constructor comment.
	 * 
	 * @param project the project to restart
	 */
	public RestartProjectAction(IProject project) {
		super(ServerUIPlugin.getResource("%actionRestartProject"));
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		// do nothing
	}
}