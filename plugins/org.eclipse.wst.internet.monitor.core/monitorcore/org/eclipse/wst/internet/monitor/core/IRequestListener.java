/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core;
/**
 * Listener for new or modified requests created from a single monitor.
 * Each request represents message traffic between a monitored client
 * and server.
 * 
 * @see IMonitor#addRequestListener(IRequestListener)
 * @since 1.0
 * 
 * [issue : CS - how come there's no requestRemoved() or requestComplete()?  Perhaps this just doesn't make sense in this context? ]
 */
public interface IRequestListener {
	/**
	 * Notification that the given request was created.
	 * <p>
	 * Requests may be created with little to no data in them.
	 * As additional information is available, the
	 * <code>requestChanged</code> method is called.
	 * </p>
	 * 
	 * @param monitor the monitor from which the request was initiated
	 * @param request the request that has been added
	 */
	public void requestAdded(IMonitor monitor, Request request);

	/**
	 * Notification that the given request has been changed.
	 * This method is called when more data is available in the
	 * request.
	 * <p>
	 * Individual values within the request are rarely modified.
	 * This method may be called when they are initially set or
	 * when more data is received from the client or server.
	 * </p>
	 * 
	 * @param monitor the monitor from which the request was initiated
	 * @param request the request that has been changed
	 */
	public void requestChanged(IMonitor monitor, Request request);
}