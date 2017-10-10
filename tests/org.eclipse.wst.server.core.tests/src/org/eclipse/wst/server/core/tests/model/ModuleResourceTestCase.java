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

import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.tests.impl.TestModuleResource;

public class ModuleResourceTestCase extends TestCase {
	protected static IModuleResource resource;

	protected IModuleResource getModuleResource() {
		if (resource == null) {
			resource = new TestModuleResource();
		}
		return resource;
	}

	public void testName() throws Exception {
		assertNull(getModuleResource().getName());
	}
	
	public void testPath() throws Exception {
		assertNull(getModuleResource().getModuleRelativePath());
	}
}