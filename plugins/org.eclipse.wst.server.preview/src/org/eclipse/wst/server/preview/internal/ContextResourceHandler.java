/*******************************************************************************
 * Copyright (c) 2008, 2021 IBM Corporation and others.
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
package org.eclipse.wst.server.preview.internal;

import java.io.IOException;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

public class ContextResourceHandler extends ResourceHandler {
	protected String context;

	public void setContext(String context) {
		this.context = context;
	}

	@Override
	public Resource getResource(String path) throws IOException {
		if (path == null || !path.startsWith(context + "/"))
			return null;
		
		path = path.substring(context.length());
		return super.getResource(path);
	}
}
