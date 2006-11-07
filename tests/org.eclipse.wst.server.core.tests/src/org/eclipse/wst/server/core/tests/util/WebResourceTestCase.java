/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.util;

import junit.framework.TestCase;
import org.eclipse.wst.server.core.util.WebResource;

public class WebResourceTestCase extends TestCase {
	protected static WebResource web;

	public void test00Create() {
		web = new WebResource(null, null);
	}
	
	public void test01GetModule() {
		assertNull(web.getModule());
	}
	
	public void test02GetPath() {
		assertNull(web.getPath());
	}
	
	public void test03ToString() {
		web.toString();
	}
}