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

import org.eclipse.wst.server.core.model.ModuleFactoryDelegate;
import org.eclipse.wst.server.core.tests.impl.TestModuleFactoryDelegate;

public class ModuleFactoryDelegateTestCase extends TestCase {
	protected static ModuleFactoryDelegate delegate;

	protected ModuleFactoryDelegate getModuleFactoryDelegate() {
		if (delegate == null) {
			delegate = new TestModuleFactoryDelegate();
		}
		return delegate;
	}

	public void testGetModuleDelegate() {
		getModuleFactoryDelegate().getModuleDelegate(null);
	}

	public void testGetModules() {
		getModuleFactoryDelegate().getModules();
	}

	public void testTestProtected() {
		((TestModuleFactoryDelegate)getModuleFactoryDelegate()).testProtectedMethods();
	}
}