/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.IOException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.internet.monitor.core.internal.provisional.ContentFilterDelegate;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
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
	 * @throws IOException if there is a connection problem
	 */
	public byte[] filter(Request request, boolean isRequest, byte[] content) throws IOException {
		if (delegate == null) {
			try {
				delegate = (ContentFilterDelegate) element.createExecutableExtension("class");
			} catch (Exception e) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Could not create content filter delegate: " + getId(), e);
				}
				return new byte[0];
			}
		}
		return delegate.filter(request, isRequest, content);
	}
}
