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

import java.util.Date;

import org.eclipse.core.runtime.IAdaptable;
/**
 * Represents a request that has been made between the client and a server.
 * The global list of known requests is available via {@link MonitorCore.getRequests()}.
 * 
 * @since 1.0
 */
public interface IRequest extends IAdaptable {
	/**
	 * Request content type (value 1) for the transport (header) of a request
	 * or response.
	 * 
	 * @see #getRequest(int)
	 * @see #getResponse(int)
	 */
	public static final int TRANSPORT = 1;
	
	/**
	 * Request content type (value 2) for the content (body) of a request
	 * or response.
	 * 
	 * @see #getRequest(int)
	 * @see #getResponse(int)
	 */
	public static final int CONTENT = 2;
	
	/**
	 * Request content type (value 3) for the entire content of a request
	 * or response.
	 * 
	 * @see #getRequest(int)
	 * @see #getResponse(int)
	 */
	public static final int ALL = 3;

	/**
	 * Return the protocol adapter of the request.
	 * 
	 * @return org.eclipse.wst.internet.monitor.core.IProtocolAdapter
	 */
	public IProtocolAdapter getProtocolAdapter();

	/**
	 * Return the date/time of this request.
	 *
	 * @return the timestamp
	 */
	public Date getDate();

	/**
	 * Returns the local (client) port.
	 *
	 * @return the local port number
	 */
	public int getLocalPort();

	/**
	 * Returns the remote (server) host.
	 *
	 * @return the remote host
	 */
	public String getRemoteHost();

	/**
	 * Returns the remote (server) port.
	 *
	 * @return the remote port number
	 */
	public int getRemotePort();

	/**
	 * Returns the request as a byte array.
	 *
	 * @param type the content type (IRequest.X)
	 * @return the request content
	 */
	public byte[] getRequest(int type);

	/**
	 * Returns the response as a byte array.
	 *
	 * @param type the content type (IRequest.X)
	 * @return the response content
	 */
	public byte[] getResponse(int type);

	/**
	 * Returns the response time in milliseconds.
	 *
	 * @return the server's response time
	 */
	public long getResponseTime();

	/**
	 * Returns a label for this request.
	 *
	 * @return the label
	 */
	public String getLabel();

	/**
	 * Add a property to the request.
	 * 
	 * @param key the key of the property to be added
	 * @param value the value of the property to be added
	 */
	public void addProperty(String key, Object value);

	/**
	 * Get a string property with a given key from the request.
	 * 
	 * @param key the key of the property
	 * @return the value of property
	 */
	public String getStringProperty(String key);

	/**
	 * Get a integer property with a given key from the request.
	 * 
	 * @param key the key of the property
	 * @return the value of property
	 */
	public Integer getIntegerProperty(String key);

	/**
	 * Get a property with a given key from the request.
	 * 
	 * @param key the key of the property
	 * @return the value of property
	 */
	public Object getObjectProperty(String key);

	/**
	 * Hook to allow other plugins that implement IRequest to fire a change event.
	 * After the internal values have changed, call this method to invoke a request
	 * change event to all registered listeners.
	 */
	public void fireChangedEvent();

	/**
	 * Add a resend request to this request.
	 * 
	 * @param request the resend request to add
	 */
	public void addResendRequest(IRequest request);

	/**
	 * Returns an array of resend requests based on this request. 
	 * 
	 * @return The array of resend requests based on this request
	 */
	public IResendRequest[] getResendRequests();
}