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

import org.eclipse.wst.server.core.internal.ModuleFolder;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;

public class ModuleFolderTestCase extends TestCase {
	protected static IModuleFolder delegate;

	public static Test suite() {
		return new OrderedTestSuite(ModuleFolderTestCase.class, "ModuleFolderTestCase");
	}

	public void test00CreateDelegate() throws Exception {
		delegate = new ModuleFolder("name", null);
	}
	
	public void test01Name() throws Exception {
		assertEquals(delegate.getName(), "name");
	}
	
	public void test02Path() throws Exception {
		assertNull(delegate.getModuleRelativePath());
	}
	
	public void test03Members() throws Exception {
		delegate.members();
	}
}