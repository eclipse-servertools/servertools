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
package org.eclipse.wst.internet.monitor.core;

import java.io.IOException;
/**
 * A content filter that filters specific contents from the monitor traffic
 * of a request.
 * The global list of known content filters is available via
 * {@link MonitorCore.getContentFilters()}. 
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
	 * Returns the relative order of this filter.
	 * Each filter has a relative order that allows them to be applied in the correct
	 * order relative to each other. Lower orders are processed first.
	 *
	 * @return the relative order
	 * 
	 * [issue: CS - The schema (contentFilterns.exsd) defines an 'order' attribute.  
	 * The comments above suggest that filters should be designed to be composable.  Is this correct?  
	 * It would be good to make this optional if the user had no idea how his filter should be ordered.
	 * I've found that sometimes 'low', 'medium', 'high' are adequate to handle ordering issues and are 
	 * easier for the extension writer deal with and get a sense of the proper value to assign. ]
	 * [issue: CS - A minor point ... in contentFilterns.exsd the 'order' attribute should have type='int' not type='string']
	 */
	//public int getOrder();

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
	public byte[] filter(Request request, boolean isRequest, byte[] content) throws IOException;
}