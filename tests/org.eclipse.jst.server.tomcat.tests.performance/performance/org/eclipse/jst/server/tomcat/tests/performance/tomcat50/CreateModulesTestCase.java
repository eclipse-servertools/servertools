/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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

import org.eclipse.jst.server.tomcat.core.tests.module.ModuleHelper;
import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.PerformanceTestCase;

public class CreateModulesTestCase extends PerformanceTestCase {
	protected static final String WEB_MODULE_NAME = "WebModule";
	protected static final int NUM_MODULES = 25;
	protected static final int NUM_RESOURCES = 500;
	protected static final int NUM_BUILDS = 10;

	public void testCreateModules() throws Exception {
		Dimension[] dims = new Dimension[] {Dimension.ELAPSED_PROCESS, Dimension.USED_JAVA_HEAP};
		tagAsSummary("Create web modules", dims);
		
		for (int i = 0; i < NUM_MODULES; i++) {
			startMeasuring();
			
			ModuleHelper.createModule(WEB_MODULE_NAME + i);
			
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
