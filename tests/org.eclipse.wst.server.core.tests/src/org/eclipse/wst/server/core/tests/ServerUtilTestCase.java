/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.internal.ResourceManager;

public class ServerUtilTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(ServerUtilTestCase.class, "ServerUtilTestCase");
	}
	
	public void testFindServer0Extension() throws Exception {
		try {
			ResourceManager.findServer(null);
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testFindServer1Extension() throws Exception {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("missingproject/test"));
		assertTrue(ResourceManager.findServer(file) == null);
	}
}