/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core.internal.provisional;

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
 * [issue: CS - I'd echo the comment above that this seems like a UI concern.  
 * I noticed there's also a viewer on the UI side, so i'm not sure how these would interact. 
 * I assume that filters operate on the byte stream and aren't concerned with UI presentation...
 * but usually UI's and filters are tightly coupled.
 * </p>
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