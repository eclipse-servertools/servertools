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
package org.eclipse.wst.monitor.core;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
/**
 * [issue: is this description correct?]
 * Represents a request that is being used by the monitor to communicate between the client and 
 * the server.
 * @since 1.0
 */
public interface IRequest extends IAdaptable{
	// [issue: should these be change to int instead of byte?]
	public static final int TRANSPORT = 1;
	public static final int CONTENT = 2;
	public static final int ALL = 3;

	/**
	 * Return the protocol adapter of the request.
	 * [issue: should we rename this to getProtocolAdapter?]
	 * 
	 * @return org.eclipse.wst.monitor.core.IProtocolAdapter
	 */
	public IProtocolAdapter getType();

	/**
	 * Return the date/time of this request.
	 *
	 * @return java.util.Date
	 */
	public Date getDate();

	/**
	 * Returns the local (client) port.
	 *
	 * @return int
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
	 * @return int
	 */
	public int getRemotePort();

	/**
	 * Returns the request as a byte array.
	 *
	 * @param type
	 * @return byte[]
	 */
	public byte[] getRequest(int type);

	/**
	 * Returns the response as a byte array.
	 *
	 * @param type
	 * @return byte[]
	 */
	public byte[] getResponse(int type);

	/**
	 * Returns the response time in milliseconds.
	 *
	 * @return long
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
	 * @param key the key of the property to be added.
	 * @param value the value of the property to be added.
	 */
	public void addProperty(String key, Object value);

	/**
	 * Get a string property with a given key from the request.
	 * 
	 * @param key the key of the property.
	 * @return the value of property.  
	 */
	public String getStringProperty(String key);

	/**
	 * Get a integer property with a given key from the request.
	 * 
	 * @param key the key of the property.
	 * @return the value of property.  
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
	 * [issue: not sure what this change event is for. Is it for the property change or for the request change.
	 *
	 */
	public void fireChangedEvent();
	
	/**
	 * Add a resend request to this request.
	 * 
	 * @param request The resend request to add
	 */
	public void addResendRequest(IRequest request);

	/**
	 * Returns an array of resend requests based on this request. 
	 * 
	 * @return The array of resend requests based on this request
	 */
	public List getResendRequests();
}