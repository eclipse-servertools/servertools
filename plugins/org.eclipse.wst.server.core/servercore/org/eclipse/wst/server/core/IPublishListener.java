/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
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
	 * @param server the server that publishing started on
	 */
	public void publishStarted(IServer server);

	/**
	 * Publishing has finished. Returns the overall status.
	 *
	 * @param server the server that publishing finished on
	 * @param status indicating what (if anything) went wrong
	 */
	public void publishFinished(IServer server, IStatus status);
}