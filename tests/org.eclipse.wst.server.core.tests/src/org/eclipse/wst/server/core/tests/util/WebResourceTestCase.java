/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
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
package org.eclipse.wst.server.core.tests.util;

import junit.framework.TestCase;
import org.eclipse.wst.server.core.util.WebResource;

public class WebResourceTestCase extends TestCase {
	protected static WebResource web;

	protected WebResource getWebResource() {
		if (web == null) {
			web = new WebResource(null, null);
		}
		return web;
	}

	public void testGetModule() {
		assertNull(getWebResource().getModule());
	}
	
	public void testGetPath() {
		assertNull(getWebResource().getPath());
	}
	
	public void testToString() {
		getWebResource().toString();
	}
}