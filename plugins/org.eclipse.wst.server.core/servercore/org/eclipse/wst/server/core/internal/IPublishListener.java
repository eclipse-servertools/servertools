/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
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
 * <p>
 * Publish listeners are added to a server via IServer.addPublishListener().
 * </p>
 * @see IServer
 * @since 1.0
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
	 * @param module
	 */
	public void publishModuleStarted(IServer server, IModule[] module);

	/**
	 * The event is fired when the module has finished publishing,
	 * and includes the status.
	 * 
	 * @param server
	 * @param module
	 * @param status
	 */
	public void publishModuleFinished(IServer server, IModule[] module, IStatus status);

	/**
	 * Publishing has finished. Returns the overall status.
	 *
	 * @param server
	 * @param status
	 */
	public void publishFinished(IServer server, IStatus status);
}