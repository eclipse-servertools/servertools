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

import org.eclipse.wst.server.core.internal.ModuleFile;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;

public class ModuleFileTestCase extends TestCase {
	protected static IModuleFile delegate;

	public static Test suite() {
		return new OrderedTestSuite(ModuleFileTestCase.class, "ModuleFileTestCase");
	}

	public void test00CreateDelegate() throws Exception {
		delegate = new ModuleFile(null, "name", null, 14);
	}
	
	public void test01Name() throws Exception {
		assertEquals(delegate.getName(), "name");
	}
	
	public void test02Path() throws Exception {
		assertNull(delegate.getModuleRelativePath());
	}
	
	public void test03Stamp() throws Exception {
		assertEquals(delegate.getModificationStamp(), 14);
	}
}