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
 * A representation of a request that is to be resent.
 * Resend requests are used when a client wants to modify and resend a
 * previously monitored request back to the server.
 * <p>
 * [issue: This violates the premise that
 * the monitor merely monitors traffic between client and server.]
 * </p>
 * <p>
 * This interface is not meant to be implemented by clients.
 * </p>
 * <p>
 * [issue: From looking at implemenation, resend request are HTTP-specific.
 * It is unclear how to make sense of these in an open-ended world of
 * protocols including simple TCP/IP.]
 * </p>
 * <p>
 * [issue: There seems to be no way for a protocol adapter to 
 * create one of these objects.]
 * </p>
 * 
 * @since 1.0
 */
public interface IResendRequest extends IRequest {
	
	/**
	 * Sends this request.
	 */
	public void sendRequest();

	/**
	 * Returns whether this request has been sent.
	 * 
	 * @return <code>true</code> if this request has been sent,
	 * and <code>false</code> otherwise
	 */
	public boolean hasBeenSent();

	/**
	 * Sets the bytes that make up the select portion of the request.
	 * <p>
	 * [issue: Again, I don't know how to explain this.
	 * </p>
	 * 
	 * @param request the request to set
	 * @param type the content type: one of {@link IRequest#TRANSPORT},
	 * {@link IRequest#CONTENT}, or {@link IRequest#ALL}
	 */
	public void setRequest(byte[] request, int type);

	/**
	 * Returns the original request that this request is based on.
	 * 
	 * @return the original request that this request is based on, or
	 *    <code>null</code> if there is none
	 */
	public IRequest getOriginalRequest();
}