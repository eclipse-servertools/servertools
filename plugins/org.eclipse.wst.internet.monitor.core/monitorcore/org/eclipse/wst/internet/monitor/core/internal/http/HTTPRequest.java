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
package org.eclipse.wst.internet.monitor.core.internal.http;

import org.eclipse.wst.internet.monitor.core.MonitorCore;
import org.eclipse.wst.internet.monitor.core.internal.Request;
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
	public HTTPRequest(int localPort, String remoteHost, int remotePort) {
		super(MonitorCore.findProtocolAdapter(MonitorCore.HTTP_PROTOCOL_ID), localPort, remoteHost, remotePort);
	}
	
	public byte[] getRequest(byte type2) {
		if (type2 == ALL)
			return request;
		else if (type2 == TRANSPORT)
			return getRequestHeader();
		else
			return getRequestContent();
	}
	
	public byte[] getResponse(byte type2) {
		if (type2 == ALL)
			return response;
		else if (type2 == TRANSPORT)
			return getResponseHeader();
		else
			return getResponseContent();
	}
	
	protected byte[] getRequestHeader() {
		Object obj = getObjectProperty(HTTP_REQUEST_HEADER);
		if (obj == null || !(obj instanceof byte[]))
			return null;
		return (byte[]) obj;
	}
	
	protected byte[] getRequestContent() {
		Object obj = getObjectProperty(HTTP_REQUEST_BODY);
		if (obj == null || !(obj instanceof byte[]))
			return null;
		return (byte[]) obj;
	}
	
	protected byte[] getResponseHeader() {
		Object obj = getObjectProperty(HTTP_RESPONSE_HEADER);
		if (obj == null || !(obj instanceof byte[]))
			return null;
		return (byte[]) obj;
	}
	
	protected byte[] getResponseContent() {
		Object obj = getObjectProperty(HTTP_RESPONSE_BODY);
		if (obj == null || !(obj instanceof byte[]))
			return null;
		return (byte[]) obj;
	}
}