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
package org.eclipse.wst.monitor.core.internal.http;

import org.eclipse.wst.monitor.core.IMonitor;
import org.eclipse.wst.monitor.core.IRequest;
import org.eclipse.wst.monitor.core.internal.Trace;

import java.util.List;
import java.util.ArrayList;
/**
 * Manages a monitor server connection between two hosts. This
 * connection may spawn one or more TCP/IP pairs to be displayed
 * in the monitor server view.
 */
public class HTTPConnection {
	protected IMonitor monitor;

	protected int req = -1;
	protected int resp = -1;

	protected List calls = new ArrayList();

	/**
	 * MonitorHTTPConnection constructor comment.
	 */
	public HTTPConnection(IMonitor monitor) {
		super();
		this.monitor = monitor;
		Trace.trace(Trace.PARSING, "TCP/IP monitor connection opened " + monitor);
	}

	/**
	 * Add a request.
	 * @param req byte[]
	 * @param isNew boolean
	 */
	public void addRequest(byte[] request, boolean isNew) {
		if (isNew)
			req ++;
		HTTPRequest pair = (HTTPRequest) getRequestResponse(req);
		pair.addToRequest(request);
	}

	/**
	 * Add a response.
	 * @param req byte[]
	 * @param isNew boolean
	 */
	public void addResponse(byte[] response, boolean isNew) {
		if (isNew)
			resp ++;
		HTTPRequest pair = (HTTPRequest) getRequestResponse(resp);
		pair.addToResponse(response);
	}

	/**
	 * 
	 */
	public void addProperty(String key, Object value) {
		IRequest pair = getRequestResponse(req);
		pair.addProperty(key, value);
	}

	/**
	 * 
	 */
	public IRequest getRequestResponse(boolean isRequest) {
		if (isRequest)
			return getRequestResponse(req);
		
		return getRequestResponse(resp);
	}

	/**
	 * 
	 */
	protected IRequest getRequestResponse(int i) {
		synchronized (this) {
			while (i >= calls.size()) {
				IRequest rr = new HTTPRequest(monitor.getLocalPort(), monitor.getRemoteHost(), monitor.getRemotePort());
				calls.add(rr);
				return rr;
			}
			return (IRequest) calls.get(i);
		}
	}

	/**
	 * Set the title
	 * @param req byte[]
	 * @param isNew boolean
	 */
	public void setLabel(String title, boolean isNew) {
		if (isNew)
			req ++;
		HTTPRequest pair = (HTTPRequest) getRequestResponse(req);
		pair.setLabel(title);
	}
}