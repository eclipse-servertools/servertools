/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.wst.server.core.IServerLifecycleEvent;

/**
 * An 
 */
public interface IServerLifecycleEventHandler {
	/**
	 * Handle module server events.
	 * 
	 * true - change has been handled (typically by calling execute() on the task)
	 *    or task should not be run.
	 * false - was not handled - let another handler deal with the change.
	 * 
	 * @return boolean[]
	 */
	public boolean[] handleModuleServerEvents(IServerLifecycleEvent[] events);
}