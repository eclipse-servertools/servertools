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

import java.io.IOException;
/**
 * A content filter that filters contents from the monitor traffic on a request.
 * 
 * @since 1.0
 */
public abstract class ContentFilterDelegate {
	/**
	 * Filter the given content from the given request. The content that has been filtered will 
	 * not be shown in the TCP/IP monitor. [issue: is this correct?]
	 * 
	 * @param request the request that the filter will be performed on.
	 * @param isRequest Set to true if the content filter applies to request monitor traffic. 
	 *    set to false if the content filter applies to the response monitor traffic. 
	 *    [issue: is this correct?]
	 * @param content - the message content to be filtered out [issue: is this correct?]
	 * @return The filtered content.
	 * @throws IOException if the filter fails to open the input of output stream of the request
	 *     [issue: is this correct?]
	 */
	public abstract byte[] filter(IRequest request, boolean isRequest, byte[] content) throws IOException;
}