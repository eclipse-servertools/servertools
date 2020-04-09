/*******************************************************************************
 * Copyright (c) 2005, 2020 IBM Corporation and others.
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
package org.eclipse.jst.server.tomcat.tests.performance.tomcat50;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.tomcat.core.tests.module.ModuleHelper;

import junit.framework.TestCase;

public class CreateXMLContentTestCase extends TestCase {
	public void testCreateWebModuleContent() throws Exception {
		for (int i = 0; i < CreateModulesTestCase.NUM_MODULES; i++) {
			final int ii = i;
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					for (int j = 0; j < CreateModulesTestCase.NUM_RESOURCES; j++)
						ModuleHelper.createXMLContent(CreateModulesTestCase.WEB_MODULE_NAME + ii, j);
				}
			}, null);
		}
	}
}
