/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.server.core.IModuleArtifactAdapter;
import org.eclipse.wst.server.core.ServerCore;

public class ModuleObjectAdaptersTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(ModuleObjectAdaptersTestCase.class, "ModuleObjectAdaptersTestCase");
	}

	public void testModuleObjectAdaptersExtension() throws Exception {
		IModuleArtifactAdapter[] moa = ServerCore.getModuleArtifactAdapters();
		if (moa != null) {
			int size = moa.length;
			for (int i = 0; i < size; i++)
				System.out.println(moa[i].getId() + " - " + moa[i].getClass());
		}
	}
}