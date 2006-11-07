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
package org.eclipse.jst.server.tomcat.tests.performance.tomcat50;

import junit.framework.TestCase;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.tests.module.ModuleHelper;

public class CreateWebContentTestCase extends TestCase {
	public void testCreateWebModuleContent() throws Exception {
		for (int i = 0; i < CreateModulesTestCase.NUM_MODULES; i++) {
			final int ii = i;
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						for (int j = 0; j < CreateModulesTestCase.NUM_RESOURCES; j++)
							ModuleHelper.createWebContent(CreateModulesTestCase.WEB_MODULE_NAME + ii, j);
					} catch (Exception e) {
						e.printStackTrace();
						throw new CoreException(new Status(IStatus.ERROR, null, 0, "Error creating resources", e));
					}
				}
			}, null);
		}
	}
}