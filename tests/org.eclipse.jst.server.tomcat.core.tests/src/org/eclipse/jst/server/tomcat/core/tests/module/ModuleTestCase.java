/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests.module;

import org.eclipse.jst.server.tomcat.core.tests.OrderedTestSuite;
import org.eclipse.jst.server.tomcat.core.tests.TomcatRuntimeTestCase;
import org.eclipse.wst.server.core.IModule;

import junit.framework.Test;
import junit.framework.TestCase;

public class ModuleTestCase extends TestCase {
	protected static final String WEB_MODULE_NAME = "MyWeb";
	public static IModule webModule;

	public static Test suite() {
		return new OrderedTestSuite(TomcatRuntimeTestCase.class, "ModuleTestCase");
	}

	public void test01CreateWebModule() throws Exception {
		ModuleHelper.createModule(WEB_MODULE_NAME);
	}

	public void test02CreateWebContent() throws Exception {
		ModuleHelper.createWebContent(WEB_MODULE_NAME, 0);
	}

	public void test04GetModule() throws Exception {
		ModuleHelper.buildFull();
		webModule = ModuleHelper.getModule(WEB_MODULE_NAME);
	}
}