/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.tests.performance.tomcat50;

import org.eclipse.jst.server.tomcat.core.tests.module.ModuleHelper;
import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.PerformanceTestCase;

public class DeleteModulesTestCase extends PerformanceTestCase {
	public void testDeleteModules() throws Exception {
		Dimension[] dims = new Dimension[] {Dimension.ELAPSED_PROCESS, Dimension.USED_JAVA_HEAP};
		tagAsSummary("Delete modules", dims);
		
		for (int i = 0; i < CreateModulesTestCase.NUM_MODULES; i++) {
			startMeasuring();
			ModuleHelper.deleteModule(CreateModulesTestCase.WEB_MODULE_NAME + i);
			stopMeasuring();
		}
		
		commitMeasurements();
		assertPerformance();
	}
}
