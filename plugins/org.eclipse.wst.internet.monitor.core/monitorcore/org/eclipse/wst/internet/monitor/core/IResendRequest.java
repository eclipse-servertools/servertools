/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
 * A representation of a request that is to be resent. This interface is not
 * meant to be implemented by clients.
 * <p>
 * Resend requests are used when a client wants to modify and resend a previously
 * monitored request back to the server.
 * </p>
 * 
 * @since 1.0
 */
public interface IResendRequest extends IRequest {
	/**
	 * Send the request
	 */
	public void sendRequest();

	/**
	 * Returns <code>true</code> if this request has been sent, and
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this request has been sent, <code>false</code> otherwise
	 */
	public boolean hasBeenSent();

	/**
	 * Set the request.
	 * 
	 * @param request the request to set
	 * @param type the type of the request to set
	 */
	public void setRequest(byte[] request, int type);

	/**
	 * Get the original request that this request is based on.
	 * 
	 * @return the original request that this request is based on, or
	 *    <code>null</code> if there is none
	 */
	public IRequest getOriginalRequest();
}