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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jst.server.tomcat.core.tests.OrderedTestSuite;
import org.eclipse.jst.server.tomcat.core.tests.TomcatRuntimeTestCase;

import junit.framework.Test;
import junit.framework.TestCase;

public class DeleteModuleTestCase extends TestCase {
	public static Test suite() {
		return new OrderedTestSuite(TomcatRuntimeTestCase.class, "DeleteModuleTestCase");
	}

	public void test0DeleteWebModule() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(ModuleTestCase.WEB_MODULE_NAME);
		project.delete(true, null);
	}
}