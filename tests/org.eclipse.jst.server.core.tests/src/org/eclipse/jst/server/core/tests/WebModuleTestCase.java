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
package org.eclipse.jst.server.core.tests;

import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.core.tests.impl.TestWebModule;
import junit.framework.Test;
import junit.framework.TestCase;

public class WebModuleTestCase extends TestCase {
	protected static IWebModule module;

	public static Test suite() {
		return new OrderedTestSuite(WebModuleTestCase.class, "WebModuleTestCase");
	}

	public void test00Create() {
		module = new TestWebModule();
	}

	public void test01SpecVersion() {
		module.getJ2EESpecificationVersion();
	}

	public void test04JSPVersion() {
		module.getJSPSpecificationVersion();
	}

	public void test05ServerVersion() {
		module.getServletSpecificationVersion();
	}

	public void test06ContextRoot() {
		module.getContextRoot();
	}
}