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
 * A content filter that filters specific contents from the monitor traffic of a request.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 1.0
 */
public interface IContentFilter {
	/**
	 * Returns the id of this filter.
	 * Each filter has a distinct, fixed id. Ids are intended to be used internally as keys;
	 * they are not intended to be shown to end users.
	 * 
	 * @return the element id
	 */
	public String getId();

	/**
	 * Returns the displayable (translated) name for this filter.
	 *
	 * @return a displayable name
	 */
	public String getName();

	/**
	 * Returns the relative order for this filter.
	 * Each filter has a relative order that allows them to be applied in the correct
	 * order relative to each other. Lower orders are processed first.
	 *
	 * @return the relative order
	 */
	public int getOrder();

	/**
	 * Filter the given content from the given request. The content that has been filtered out will 
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
	public byte[] filter(IRequest request, boolean isRequest, byte[] content) throws IOException;
}