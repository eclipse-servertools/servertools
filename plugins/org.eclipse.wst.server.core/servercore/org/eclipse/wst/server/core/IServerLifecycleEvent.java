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
package org.eclipse.wst.server.core;

import org.eclipse.wst.server.core.model.IModuleEvent;
import org.eclipse.wst.server.core.model.IModuleFactoryEvent;
/**
 * A server lifecycle event. This even is fired whenever changes to modules
 * within the workspace require changes (fixes) to the given server.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerLifecycleEvent {
	public IServer getServer();

	public IModuleFactoryEvent[] getModuleFactoryEvents();

	public IModuleEvent[] getModuleEvents();
	
	public ITask[] getTasks();
}