/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.internet.monitor.core.IRequest;
import org.eclipse.wst.internet.monitor.core.IResendRequest;
/**
 * A single TCP/IP request/response pair.
 */
public class Request implements IRequest {
	protected Monitor monitor;
	protected Date date;
	protected long responseTime = -1;
	protected int localPort;
	protected String remoteHost;
	protected int remotePort;
	protected byte[] request;
	protected byte[] response;
	
	protected String label;
	protected String protocolId;

	protected Properties properties;
	
	protected List resendRequests = new ArrayList();

	/**
	 * RequestResponse constructor comment.
	 */
	public Request(Monitor monitor, String protocolId, int localPort, String remoteHost, int remotePort) {
		super();
		this.monitor = monitor;
		this.protocolId = protocolId;
		this.localPort = localPort;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		date = new Date();
		properties = new Properties();
		monitor.addRequest(this);
	}

	public String getProtocol() {
		return protocolId;
	}

	/**
	 * Add to the request.
	 *
	 * @param addRequest byte[]
	 */
	public void addToRequest(byte[] addRequest) {
		if (addRequest == null || addRequest.length == 0)
			return;
	
		if (request == null || request.length == 0) {
			setRequest(addRequest);
			return;
		}
	
		int size = request.length + addRequest.length;
		byte[] b = new byte[size];
		System.arraycopy(request, 0, b, 0, request.length);
		System.arraycopy(addRequest, 0, b, request.length, addRequest.length);
		request = b;
	}

	/**
	 * Add to the response.
	 *
	 * @param addResponse byte[]
	 */
	public void addToResponse(byte[] addResponse) {
		if (addResponse == null || addResponse.length == 0)
			return;
	
		if (response == null || response.length == 0) {
			setResponse(addResponse);
			return;
		}
	
		int size = response.length + addResponse.length;
		byte[] b = new byte[size];
		System.arraycopy(response, 0, b, 0, response.length);
		System.arraycopy(addResponse, 0, b, response.length, addResponse.length);
		response = b;
	}

	/**
	 * Return the date/time of this request.
	 *
	 * @return java.util.Date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the local port.
	 *
	 * @return int
	 */
	public int getLocalPort() {
		return localPort;
	}

	/**
	 * Returns the remote host.
	 *
	 * @return java.lang.String
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * Returns the remote port.
	 *
	 * @return int
	 */
	public int getRemotePort() {
		return remotePort;
	}

	/**
	 * Returns the request as a byte array.
	 *
	 * @return byte[]
	 */
	public byte[] getRequest(int type2) {
		return request;
	}

	/**
	 * Returns the response as a byte array.
	 *
	 * @return byte[]
	 */
	public byte[] getResponse(int type2) {
		return response;
	}

	/**
	 * Returns the response time in milliseconds.
	 *
	 * @return long
	 */
	public long getResponseTime() {
		return responseTime;
	}

	/**
	 * Returns the title, if one exists.
	 *
	 * @return java.lang.String
	 */
	public String getLabel() {
		if (label == null)
			return getRemoteHost() + ":" + getRemotePort();
		
		return label;
	}

	/**
	 * Set the request.
	 *
	 * @param request byte[]
	 */
	protected void setRequest(byte[] request) {
		if (request == null || request.length == 0)
			return;
	
		this.request = request;
	
		monitor.requestChanged(this);
	}

	/**
	 * Set the response.
	 *
	 * @param response byte[]
	 */
	protected void setResponse(byte[] response) {
		if (response == null || response.length == 0)
			return;
	
		this.response = response;
	
		responseTime = System.currentTimeMillis() - date.getTime();
	
		monitor.requestChanged(this);
	}

	/**
	 * Sets the title.
	 *
	 * @param s java.lang.String
	 */
	public void setLabel(String s) {
		// property can only be set once
		if (label != null)
			return;
	
		label = s;
		monitor.requestChanged(this);
	}
	
	/**
	 * 
	 */
	public void setProperty(String key, Object value) {
		try {
			if (properties.containsKey(key))
				properties.remove(key);
			if (value != null)
				properties.put(key, value);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not add property", e);
		}
	}

	/**
	 * 
	 */
	public Object getProperty(String key) {
		try {
			return properties.get(key);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Monitor getMonitor() {
		return monitor;
	}
	
	public void fireChangedEvent() {
		monitor.requestChanged(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.core.IRequest#addResendRequest(org.eclipse.wst.internet.monitor.core.IRequest)
	 */
	public void addResendRequest(IRequest resendReq) {
		if (resendReq != null)
			resendRequests.add(resendReq);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.core.IRequest#getResendRequests()
	 */
	public IResendRequest[] getResendRequests() {
		IResendRequest[] rr = new IResendRequest[resendRequests.size()];
		resendRequests.toArray(rr);
		return rr;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
    }
}