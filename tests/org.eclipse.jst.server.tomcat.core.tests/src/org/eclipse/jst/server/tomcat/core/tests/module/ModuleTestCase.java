/*******************************************************************************
 * Copyright (c) 2004, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests.module;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.server.core.IModule;

public class ModuleTestCase extends TestCase {
	protected static final String WEB_MODULE_NAME = "MyWeb";
	protected static final String CLOSED_PROJECT = "ClosedProject";
	public static IModule webModule;

	// This test suite ensures the test methods are run in order
	public static TestSuite getOrderedTests() {
		TestSuite mySuite = new TestSuite();
		mySuite.addTest(TestSuite.createTest(ModuleTestCase.class, "test00ClosedProject"));
		mySuite.addTest(TestSuite.createTest(ModuleTestCase.class, "test01CreateWebModule"));
		mySuite.addTest(TestSuite.createTest(ModuleTestCase.class, "test02CreateWebContent"));
		mySuite.addTest(TestSuite.createTest(ModuleTestCase.class, "test04GetModule"));
		mySuite.addTest(TestSuite.createTest(ModuleTestCase.class, "test05CountFilesInModule"));
		return mySuite;
	}	
	
	public void test00ClosedProject() throws Exception {
		ModuleHelper.createClosedProject(CLOSED_PROJECT);
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

	public void test05CountFilesInModule() throws Exception {
		assertEquals(ModuleHelper.countFilesInModule(webModule), 3);
	}
}