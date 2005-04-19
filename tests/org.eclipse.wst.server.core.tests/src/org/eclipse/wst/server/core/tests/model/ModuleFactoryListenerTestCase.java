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

import org.eclipse.wst.server.core.model.IModuleFactoryListener;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.tests.impl.TestModuleFactoryListener;

public class ModuleFactoryListenerTestCase extends TestCase {
	protected static IModuleFactoryListener listener;

	public static Test suite() {
		return new OrderedTestSuite(ModuleFactoryListenerTestCase.class, "ModuleFactoryListenerTestCase");
	}

	public void test00CreateListener() throws Exception {
		listener = new TestModuleFactoryListener();
	}
	
	public void test01ModuleFactoryChanged() throws Exception {
		listener.moduleFactoryChanged(null);
	}
}