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
package org.eclipse.wst.internet.monitor.core.internal.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

import org.eclipse.wst.internet.monitor.core.IRequest;
import org.eclipse.wst.internet.monitor.core.IResendRequest;
import org.eclipse.wst.internet.monitor.core.internal.Connection;
import org.eclipse.wst.internet.monitor.core.internal.SocketWrapper;
/**
 * Wraps an existing request to create an HTTP request that can be sent. The
 * response is ignored. Only the request is important in this case.
 */
public class ResendHTTPRequest extends HTTPRequest implements IResendRequest {
	private boolean sent = false;

	private byte[] header;

	private byte[] content;

	private IRequest originalRequest = null;

	/**
	 * Constructor.
	 * 
	 * @param req the request that is to be resent.
	 */
	public ResendHTTPRequest(IRequest req) {
		super(req.getLocalPort(), req.getRemoteHost(), req.getRemotePort());
		addProperty(HTTP_REQUEST_HEADER, req
				.getObjectProperty(HTTP_REQUEST_HEADER));
		addProperty(HTTP_REQUEST_BODY, req.getObjectProperty(HTTP_REQUEST_BODY));
		header = req.getRequest(TRANSPORT);
		content = req.getRequest(CONTENT);
		request = req.getRequest(ALL);
		label = req.getLabel();
		this.originalRequest = req;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.core.IResendRequest#sendRequest()
	 */
	public void sendRequest() {
		try {
			Socket inSocket = new SocketWrapper(new ByteArrayInputStream(
					request));
			Socket outSocket = new Socket(remoteHost, remotePort);
			//Connection conn = new Connection(inSocket, outSocket);
			//DefaultThread requestThread = new DefaultThread(conn, this, in,
			// outSocket.getOutputStream(), true);
			//requestThread.start();
			//new DefaultThread(conn, this, outSocket.getInputStream(),
			// inSocket.getOutputStream(), false).start();
			Connection conn2 = new Connection(inSocket, outSocket);
			ResendHTTPConnection conn = new ResendHTTPConnection(this);

			HTTPThread request2 = new HTTPThread(conn2, inSocket
					.getInputStream(), outSocket.getOutputStream(), conn, true,
					remoteHost, remotePort);
			HTTPThread response2 = new HTTPThread(conn2, outSocket
					.getInputStream(), inSocket.getOutputStream(), conn, false,
					"localhost", localPort, request2);
			request2.start();
			response2.start();
		} catch (IOException e) {
			response = ("Unable to resend to server.\n" + e).getBytes();
		}
		sent = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.core.internal.Request#addToRequest(byte[])
	 */
	public void addToRequest(byte[] addRequest) {
		// Don't want to add to the request as we already have the request.
	}

	public boolean hasBeenSent() {
		return sent;
	}

	public void setRequest(byte[] request, int type) {
		if (type == TRANSPORT && request != null)
			header = request;
		else if (type == CONTENT)
			content = request;
		
		int length = 0;
		int headerLength = 0;
		if (header != null) {
			length += header.length;
			headerLength = length;
		}
		if (content != null)
			length += content.length;
		byte[] newRequest = new byte[length];
		if (header != null)
			System.arraycopy(header, 0, newRequest, 0, header.length);
		if (content != null)
			System.arraycopy(content, 0, newRequest, headerLength, content.length);
		super.setRequest(newRequest);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.core.internal.http.HTTPRequest#getRequestContent()
	 */
	protected byte[] getRequestContent() {
		return content;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.core.internal.http.HTTPRequest#getRequestHeader()
	 */
	protected byte[] getRequestHeader() {
		return header;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.core.IResendRequest#getOriginalRequest()
	 */
	public IRequest getOriginalRequest() {
		return originalRequest;
	}
}