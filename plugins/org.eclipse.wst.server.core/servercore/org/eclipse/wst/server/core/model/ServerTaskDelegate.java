/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import java.util.List;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IOptionalTask;
import org.eclipse.wst.server.core.IServer;
/**
 * A server task delegate.
 * 
 * <p>This is the implementation of a serverTask extension point.</p>
 */
public abstract class ServerTaskDelegate {
	/**
	 * Returns the tasks that should be performed on the server.
	 * 
	 * @return the tasks that should be performed on the server.
	 */
	public abstract IOptionalTask[] getTasks(IServer server, List[] parents, IModule[] modules);
}