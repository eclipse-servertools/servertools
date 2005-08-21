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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.tests.module.ModuleHelper;
import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.PerformanceTestCase;

public class CreateJavaContentTestCase extends PerformanceTestCase {
	public static Test suite() {
		return new TestSuite(CreateJavaContentTestCase.class, "CreateJavaContentTestCase");
	}

	public void testCreateModuleJavaContent() throws Exception {
		Dimension[] dims = new Dimension[] {Dimension.ELAPSED_PROCESS, Dimension.USED_JAVA_HEAP};
		tagAsGlobalSummary("Create module Java content", dims);
		
		for (int i = 0; i < CreateModulesTestCase.NUM_MODULES; i++) {
			startMeasuring();
			final int ii = i;
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						for (int j = 0; j < CreateModulesTestCase.NUM_RESOURCES; j++)
							ModuleHelper.createJavaContent(CreateModulesTestCase.WEB_MODULE_NAME + ii, j);
					} catch (Exception e) {
						e.printStackTrace();
						throw new CoreException(new Status(IStatus.ERROR, null, 0, "Error creating resources", e));
					}
				}
			}, null);
			stopMeasuring();
		}
		
		commitMeasurements();
		assertPerformance();
	}
}