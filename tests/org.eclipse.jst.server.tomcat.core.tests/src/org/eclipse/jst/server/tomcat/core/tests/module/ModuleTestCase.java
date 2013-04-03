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

import org.eclipse.wst.server.core.IModule;

import junit.framework.TestCase;

public class ModuleTestCase extends TestCase {
	protected static final String WEB_MODULE_NAME = "MyWeb";
	protected static final String CLOSED_PROJECT = "ClosedProject";
	public static IModule webModule;

	public void testAll() throws Exception {
		ModuleHelper.createClosedProject(CLOSED_PROJECT);

		ModuleHelper.createModule(WEB_MODULE_NAME);

		ModuleHelper.createWebContent(WEB_MODULE_NAME, 0);

		ModuleHelper.buildFull();
		webModule = ModuleHelper.getModule(WEB_MODULE_NAME);

		assertEquals(ModuleHelper.countFilesInModule(webModule), 3);
	}
}