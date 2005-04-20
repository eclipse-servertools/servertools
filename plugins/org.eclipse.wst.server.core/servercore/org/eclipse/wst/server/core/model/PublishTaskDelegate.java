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
package org.eclipse.wst.server.core.model;

import java.util.List;

import org.eclipse.wst.server.core.IOptionalTask;
import org.eclipse.wst.server.core.IServer;
/**
 * A publish task delegate.
 * 
 * <p>This is the implementation of a publishTask extension point.</p>
 * 
 * @since 1.0
 */
public abstract class PublishTaskDelegate {
	/**
	 * Returns the tasks that should be performed during publishing.
	 * 
	 * @param server the server
	 * @param modules a list containing IModule arrays
	 * @return the tasks that should be performed on the server.
	 */
	public abstract IOptionalTask[] getTasks(IServer server, List modules);
}