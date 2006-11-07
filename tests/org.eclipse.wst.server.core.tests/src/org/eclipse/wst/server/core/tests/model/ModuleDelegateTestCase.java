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

import junit.framework.TestCase;

import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.tests.impl.TestModuleDelegate;

public class ModuleDelegateTestCase extends TestCase {
	protected static ModuleDelegate delegate;

	public void test00CreateDelegate() throws Exception {
		delegate = new TestModuleDelegate();
	}
	
	public void test01Initialize() throws Exception {
		delegate.initialize();
	}
	
	public void test02Initialize() throws Exception {
		delegate.initialize(null);
	}
	
	public void test03GetChildModules() throws Exception {
		delegate.getChildModules();
	}
	
	public void test04GetModule() throws Exception {
		delegate.getModule();
	}
	
	public void test05Validate() throws Exception {
		delegate.validate();
	}
	
	public void test06Members() throws Exception {
		delegate.members();
	}
}