/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.IStatus;
/**
 * A publish listener is used to listen for publishing events from a server.
 * The events are typically received in the following order:
 * 
 * publishStarted() - The publish operation is starting
 *   publishModuleStarted() - This section is fired for each module in the server
 *   publishModuleFinished() - This module is finished publishing
 *   publishModuleStarted() - The next module is publishing
 *   ...
 * publishFinished() - The publish operation is finished
 */
public interface IPublishListener {
	/**
	 * Fired to notify that publishing has begun.
	 *
	 * @param server
	 */
	public void publishStarted(IServer server);

	/**
	 * Fired with the module to notify that publishing of this module
	 * is starting.
	 * 
	 * @param server
	 * @param parents
	 * @param module
	 */
	public void publishModuleStarted(IServer server, IModule[] parents, IModule module);

	/**
	 * The event is fired when the module has finished publishing,
	 * and includes the status.
	 * 
	 * @param server
	 * @param parents
	 * @param module
	 * @param status
	 */
	public void publishModuleFinished(IServer server, IModule[] parents, IModule module, IStatus status);

	/**
	 * Publishing has finished. Returns the overall status.
	 *
	 * @param server
	 * @param status
	 */
	public void publishFinished(IServer server, IStatus status);
}