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
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;

public class WebResourceTestCase extends TestCase {
	protected static WebResource web;
	
	public static Test suite() {
		return new OrderedTestSuite(WebResourceTestCase.class, "WebResourceTestCase");
	}

	public void test00Create() {
		web = new WebResource(null, null);
	}
	
	public void test01GetModule() {
		assertNull(web.getModule());
	}
	
	public void test02GetPath() {
		assertNull(web.getPath());
	}
}