/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
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

	protected ModuleDelegate getModuleDelegate() {
		if (delegate == null) {
			delegate = new TestModuleDelegate();
		}
		return delegate;
	}
	public void testInitialize() throws Exception {
		getModuleDelegate().initialize();
	}
	
	public void testInitialize2() throws Exception {
		getModuleDelegate().initialize(null);
	}
	
	public void testGetChildModules() throws Exception {
		getModuleDelegate().getChildModules();
	}
	
	public void testGetModule() throws Exception {
		getModuleDelegate().getModule();
	}
	
	public void testValidate() throws Exception {
		getModuleDelegate().validate();
	}
	
	public void testMembers() throws Exception {
		getModuleDelegate().members();
	}
}