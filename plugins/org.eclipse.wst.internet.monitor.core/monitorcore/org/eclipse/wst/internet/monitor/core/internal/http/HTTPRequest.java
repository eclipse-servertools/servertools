/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal.http;

import org.eclipse.wst.internet.monitor.core.Request;
import org.eclipse.wst.internet.monitor.core.internal.IProtocolAdapter;
import org.eclipse.wst.internet.monitor.core.internal.Monitor;
/**
 * 
 */
public class HTTPRequest extends Request {
	protected static final String HTTP_REQUEST_HEADER = "request-header";
	protected static final String HTTP_RESPONSE_HEADER = "response-header";

	protected static final String HTTP_REQUEST_BODY = "request-body";
	protected static final String HTTP_RESPONSE_BODY = "response-body";
	
	protected static final byte[] EMPTY = new byte[0];

	/**
	 * HTTPRequestResponse constructor comment.
	 */
	public HTTPRequest(Monitor monitor, int localPort, String remoteHost, int remotePort) {
		super(monitor, IProtocolAdapter.HTTP_PROTOCOL_ID, localPort, remoteHost, remotePort);
	}

	public byte[] getRequest(int type2) {
		if (type2 == ALL)
			return request;
		else if (type2 == TRANSPORT)
			return getRequestHeader();
		else
			return getRequestContent();
	}
	
	public byte[] getResponse(int type2) {
		if (type2 == ALL)
			return response;
		else if (type2 == TRANSPORT)
			return getResponseHeader();
		else
			return getResponseContent();
	}
	
	protected byte[] getRequestHeader() {
		Object obj = getProperty(HTTP_REQUEST_HEADER);
		if (obj == null || !(obj instanceof byte[]))
			return null;
		return (byte[]) obj;
	}
	
	protected byte[] getRequestContent() {
		Object obj = getProperty(HTTP_REQUEST_BODY);
		if (obj == null || !(obj instanceof byte[]))
			return null;
		return (byte[]) obj;
	}
	
	protected byte[] getResponseHeader() {
		Object obj = getProperty(HTTP_RESPONSE_HEADER);
		if (obj == null || !(obj instanceof byte[]))
			return null;
		return (byte[]) obj;
	}
	
	protected byte[] getResponseContent() {
		Object obj = getProperty(HTTP_RESPONSE_BODY);
		if (obj == null || !(obj instanceof byte[]))
			return null;
		return (byte[]) obj;
	}
	
	protected void setName(String n) {
		super.setName(n);
	}
}