/*******************************************************************************
 * Copyright (c) 2006, 2020 IBM Corporation and others.
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

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.tests.module.ModuleHelper;
import org.eclipse.wst.server.core.internal.ServerPlugin;

import junit.framework.TestCase;

public class CreateHugeModuleTestCase extends TestCase {
	protected static final String WEB_MODULE_NAME = "HugeModule";
	protected static final int NUM_RESOURCES = 2000;
	protected static final int NUM_EXTERNAL_JARS = 100;

	public void testCreateHugeWebModule() throws Exception {
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				try {
					ModuleHelper.createModule(WEB_MODULE_NAME);
					for (int i = 0; i < NUM_RESOURCES; i++)
						ModuleHelper.createWebContent(WEB_MODULE_NAME, i);
					for (int i = 0; i < NUM_RESOURCES; i++)
						ModuleHelper.createJavaContent(WEB_MODULE_NAME, i);
					for (int i = 0; i < NUM_RESOURCES; i++)
						ModuleHelper.createXMLContent(WEB_MODULE_NAME, i);
					
					// add external jars
					IPath path = ServerPlugin.getInstance().getStateLocation().append("jars");
					ModuleHelper.createJarContent(WEB_MODULE_NAME, NUM_EXTERNAL_JARS, path);
				} catch (ExecutionException e) {
					e.printStackTrace();
					throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.jst.server.tomcat.tests.performance", 0, "ExecutionException creating resources", e));
				} catch (IOException e) {
					e.printStackTrace();
					throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.jst.server.tomcat.tests.performance", 0, "IOException creating resources", e));
				}
			}
		}, null);
		
		ModuleHelper.buildIncremental();
	}
}