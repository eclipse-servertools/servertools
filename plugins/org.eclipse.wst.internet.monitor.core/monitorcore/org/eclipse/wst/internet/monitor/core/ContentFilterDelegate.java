/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core;

import java.io.IOException;
/**
 * A content filter that filters contents from the monitor traffic on a request.
 * <p>
 * This abstract class is intended to be subclassed only by clients
 * to extend the <code>contentFilters</code> extension point.
 * The subclass must have a public 0-argument constructor, which will be used
 * automatically to instantiate the delegate when required. 
 * </p>
 * <p>
 * [issue: The notion of content filters is a UI/presentation
 * concern, not something that is makes sense to have as core
 * functionality. The contentFilters extension point, IContentFilter,
 * and ContentFilterDelegate should all move to the o.e.wst.internet.monitor.ui
 * plug-in.]
 * </p>
 * 
 * @since 1.0
 */
public abstract class ContentFilterDelegate {
	/**
	 * Filter the given content from the given request. The content that has been filtered out will 
	 * not be shown to clients of the TCP/IP monitor.
	 * 
	 * @param request the request that the filter will be performed on
	 * @param isRequest set to true if the content filter applies to request monitor traffic,
	 *    or set to false if the content filter applies to the response monitor traffic
	 * @param content the message content to be filtered out
	 * @return the filtered content
	 * @throws IOException if there is an error while parsing or filtering the content
	 */
	public abstract byte[] filter(Request request, boolean isRequest, byte[] content) throws IOException;
}