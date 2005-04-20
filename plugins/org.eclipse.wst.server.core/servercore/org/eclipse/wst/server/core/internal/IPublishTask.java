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
package org.eclipse.wst.server.core.internal;

import java.util.List;
import org.eclipse.wst.server.core.IOptionalTask;
import org.eclipse.wst.server.core.IServer;
/**
 * A task for a server.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @since 1.0
 */
public interface IPublishTask {
	/**
	 * Returns the id of the adapter.
	 *
	 * @return java.lang.String
	 */
	public String getId();

	/**
	 * Returns true if the given type (given by the id) can use this task. This
	 * result is based on the result of the getTypeIds() method.
	 * 
	 * @param id a server type id
	 * @return boolean
	 */
	public boolean supportsType(String id);

	/**
	 * Lets the task know that it is about to be used. This method should
	 * be used to clean out any previously cached information, or start to
	 * create a new cache.
	 * 
	 * @param server the server
	 * @param modules a list containing IModule arrays
	 * @return a possibly empty array of optional tasks 
	 */
	public IOptionalTask[] getTasks(IServer server, List modules);
}