/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.monitor.core.internal;

import java.io.IOException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.monitor.core.ContentFilterDelegate;
import org.eclipse.wst.monitor.core.IContentFilter;
import org.eclipse.wst.monitor.core.IRequest;
/**
 * 
 */
public class ContentFilter implements IContentFilter {
	protected IConfigurationElement element;
	protected ContentFilterDelegate delegate;
	
	protected ContentFilter(IConfigurationElement element) {
		this.element = element;
	}

	public String getId() {
		return element.getAttribute("id");
	}
	
	/*
	 * [issue: I couldn't find any place that is using the order]
	 */ 
	public int getOrder() {
		try {
			return Integer.parseInt(element.getAttribute("order"));
		} catch (Exception e) {
			return 0;
		}
	}

	public String getName() {
		return element.getAttribute("name");
	}

	public byte[] filter(IRequest request, boolean isRequest, byte[] content) throws IOException {
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