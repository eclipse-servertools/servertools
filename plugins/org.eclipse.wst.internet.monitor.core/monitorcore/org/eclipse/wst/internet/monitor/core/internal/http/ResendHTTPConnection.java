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

import org.eclipse.wst.internet.monitor.core.IRequest;
/**
 * A resend HTTP connection wraps an HTTP connection to send an existing request.
 */
public class ResendHTTPConnection extends HTTPConnection{
	protected IRequest existingRequest;

	public ResendHTTPConnection(IRequest request) {
		super(null);
		this.existingRequest = request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.core.internal.http.HTTPConnection#getRequestResponse(int)
	 */
	protected IRequest getRequestResponse(int i) {
		return existingRequest;
	}
}