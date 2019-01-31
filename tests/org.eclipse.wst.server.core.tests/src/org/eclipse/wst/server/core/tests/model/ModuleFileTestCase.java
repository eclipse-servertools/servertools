/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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

	protected IModuleFile getModuleFile() {
		if (delegate == null) {
			delegate = new ModuleFile((IFile)null, "name", null);
		}
		return delegate;
	}

	public void testName() throws Exception {
		assertEquals(getModuleFile().getName(), "name");
	}

	public void testPath() throws Exception {
		assertNull(getModuleFile().getModuleRelativePath());
	}

	public void testStamp() throws Exception {
		assertEquals(getModuleFile().getModificationStamp(), -1);
	}
}