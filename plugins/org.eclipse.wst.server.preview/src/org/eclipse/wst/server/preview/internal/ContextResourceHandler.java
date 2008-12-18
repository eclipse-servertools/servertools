/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.internal;

import java.net.MalformedURLException;

import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.resource.Resource;

public class ContextResourceHandler extends ResourceHandler {
	protected String context;

	public void setContext(String context) {
		this.context = context;
	}

	public Resource getResource(String path) throws MalformedURLException {
		if (path == null || !path.startsWith(context + "/"))
			return null;
		
		path = path.substring(context.length());
		return super.getResource(path);
	}
}
