/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core.internal.http;

import org.eclipse.wst.internet.monitor.core.internal.Monitor;
import org.eclipse.wst.internet.monitor.core.internal.Trace;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitor;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;

import java.util.List;
import java.util.ArrayList;
/**
 * Manages a monitor server connection between two hosts. This
 * connection may spawn one or more TCP/IP requests to be displayed
 * in the monitor server view.
 */
public class HTTPConnection {
	protected IMonitor monitor;

	protected int req = -1;
	protected int resp = -1;

	protected List requests = new ArrayList();

	/**
	 * HTTPConnection constructor comment.
	 * 
	 * @param monitor a monitor
	 */
	public HTTPConnection(IMonitor monitor) {
		super();
		this.monitor = monitor;
		Trace.trace(Trace.PARSING, "TCP/IP monitor connection opened " + monitor);
	}

	/**
	 * Add a request.
	 * 
	 * @param b the request data
	 * @param isNew true if new
	 */
	public void addRequest(byte[] b, boolean isNew) {
		if (isNew)
			req ++;
		HTTPRequest request = (HTTPRequest) getRequestResponse(req);
		request.addToRequest(b);
	}

	/**
	 * Add a response.
	 * 
	 * @param b the response data
	 * @param isNew true if new
	 */
	public void addResponse(byte[] b, boolean isNew) {
		if (isNew)
			resp ++;
		HTTPRequest request = (HTTPRequest) getRequestResponse(resp);
		request.addToResponse(b);
	}

	/**
	 * Add a property.
	 * 
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, Object value) {
		Request request = getRequestResponse(req);
		request.setProperty(key, value);
	}

	/**
	 * Get the request.
	 * 
	 * @param isRequest
	 * @return the request
	 */
	public Request getRequestResponse(boolean isRequest) {
		if (isRequest)
			return getRequestResponse(req);
		
		return getRequestResponse(resp);
	}

	/**
	 * 
	 */
	protected Request getRequestResponse(int i) {
		synchronized (this) {
			while (i >= requests.size()) {
				Request request = new HTTPRequest((Monitor) monitor, monitor.getLocalPort(), monitor.getRemoteHost(), monitor.getRemotePort());
				requests.add(request);
				return request;
			}
			return (Request) requests.get(i);
		}
	}

	/**
	 * Set the title.
	 * 
	 * @param title the title
	 * @param isNew boolean
	 */
	public void setLabel(String title, boolean isNew) {
		if (isNew)
			req ++;
		HTTPRequest request = (HTTPRequest) getRequestResponse(req);
		request.setName(title);
	}
}