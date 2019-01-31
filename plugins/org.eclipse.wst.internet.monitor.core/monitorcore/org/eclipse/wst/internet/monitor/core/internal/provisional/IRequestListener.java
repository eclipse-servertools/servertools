/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal.provisional;
/**
 * Listener for new or modified requests created from a single monitor.
 * Each request represents message traffic between a monitored client
 * and server.
 * <p>
 * Requests are not persisted - they only exist in this API until the
 * connection is done. An initial requestAdded() event is fired when the
 *	request is created (by the client creating a connection), and then
 * requestChanged() events occur as data is passed through the request
 * or changes are made to the request's properties.
 * </p>
 * 
 * @see IMonitor#addRequestListener(IRequestListener)
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
