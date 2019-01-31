/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal.http;

import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
/**
 * A resend HTTP connection wraps an HTTP connection to send an existing request.
 */
public class ResendHTTPConnection extends HTTPConnection {
	protected Request existingRequest;

	/**
	 * Create a new resend connection.
	 * 
	 * @param request
	 */
	public ResendHTTPConnection(Request request) {
		super(null);
		this.existingRequest = request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.core.internal.http.HTTPConnection#getRequestResponse(int)
	 */
	protected Request getRequestResponse(int i) {
		return existingRequest;
	}
}