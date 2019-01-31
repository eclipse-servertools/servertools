/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
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
package org.eclipse.jst.server.tomcat.core.tests.module;

import junit.framework.TestCase;

public class DeleteModuleTestCase extends TestCase {
	public void test00DeleteWebModule() throws Exception {
		ModuleHelper.deleteModule(ModuleTestCase.WEB_MODULE_NAME);
	}

	public void test01DeleteClosedProject() throws Exception {
		ModuleHelper.deleteModule(ModuleTestCase.CLOSED_PROJECT);
	}
}