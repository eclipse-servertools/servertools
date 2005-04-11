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
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.IOException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.internet.monitor.core.ContentFilterDelegate;
import org.eclipse.wst.internet.monitor.core.Request;
/**
 * 
 */
public class ContentFilter implements IContentFilter {
	protected IConfigurationElement element;
	protected ContentFilterDelegate delegate;
	
	protected ContentFilter(IConfigurationElement element) {
		this.element = element;
	}

	/**
	 * Return the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	/**
	 * Returns the relative order for this filter.
	 * 
	 * @return the order
	 */ 
	public int getOrder() {
		try {
			return Integer.parseInt(element.getAttribute("order"));
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return element.getAttribute("name");
	}

	/**
	 * Do the filtering.
	 * 
	 * @param request the request
	 * @param isRequest true if request, false if response
	 * @param content the content
	 * @return the filtered content
	 * @throws IOException if there is a connection issue
	 */
	public byte[] filter(Request request, boolean isRequest, byte[] content) throws IOException {
		if (delegate == null) {
			try {
				delegate = (ContentFilterDelegate) element.createExecutableExtension("class");
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create content filter delegate: " + getId(), e);
				return new byte[0];
			}
		}
		return delegate.filter(request, isRequest, content);
	}
}