/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.util.ModuleFile;

public class ModuleFileTestCase extends TestCase {
	protected static IModuleFile delegate;

	public void test00CreateDelegate() throws Exception {
		delegate = new ModuleFile((IFile)null, "name", null);
	}

	public void test01Name() throws Exception {
		assertEquals(delegate.getName(), "name");
	}

	public void test02Path() throws Exception {
		assertNull(delegate.getModuleRelativePath());
	}

	public void test03Stamp() throws Exception {
		assertEquals(delegate.getModificationStamp(), -1);
	}
}