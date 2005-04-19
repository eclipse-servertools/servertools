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
package org.eclipse.wst.server.core.tests.model;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.wst.server.core.model.ModuleFactoryDelegate;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.tests.impl.TestModuleFactoryDelegate;

public class ModuleFactoryDelegateTestCase extends TestCase {
	protected static ModuleFactoryDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(ModuleFactoryDelegateTestCase.class, "ModuleFactoryDelegateTestCase");
	}

	public void test00CreateDelegate() throws Exception {
		delegate = new TestModuleFactoryDelegate();
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

	public void test07TestProtected() {
		((TestModuleFactoryDelegate)delegate).testProtectedMethods();
	}
}