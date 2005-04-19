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

import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.tests.impl.TestProjectModuleFactoryDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;
import junit.framework.Test;
import junit.framework.TestCase;

public class ProjectModuleFactoryDelegateTestCase extends TestCase {
	protected static ProjectModuleFactoryDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(ProjectModuleFactoryDelegateTestCase.class, "ProjectModuleFactoryDelegateTestCase");
	}

	public void test00Create() {
		delegate = new TestProjectModuleFactoryDelegate();
	}
	
	public void test01Initialize() {
		delegate.initialize(null);
	}

	public void test02CreateModule() {
		delegate.createModule(null, null, null, null, null);
	}

	public void test03GetModuleDelegate() {
		delegate.getModuleDelegate(null);
	}

	public void test04GetModules() {
		delegate.getModules();
	}

	public void test05AddModuleFactoryListener() {
		delegate.addModuleFactoryListener(null);
	}

	public void test06RemoveModuleFactoryListener() {
		delegate.removeModuleFactoryListener(null);
	}

	public void test07GetModules() {
		delegate.getModules(null);
	}

	public void test08TestProtected() throws Exception {
		((TestProjectModuleFactoryDelegate)delegate).testProtected();
	}
}