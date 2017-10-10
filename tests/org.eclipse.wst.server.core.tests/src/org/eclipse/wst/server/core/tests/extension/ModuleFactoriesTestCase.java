/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.TestCase;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.server.core.ServerUtil;

public class ModuleFactoriesTestCase extends TestCase {
	public void testModuleFactoriesExtension() throws Exception {
		// get modules
		try {
			ServerUtil.getModule((IProject) null);
		} catch (Exception e) {
			// ignore
		}
	}
}