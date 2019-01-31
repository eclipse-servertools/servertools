/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
package org.eclipse.wst.server.tests.performance;

import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.PerformanceTestCase;
import org.eclipse.wst.server.core.internal.ModuleFactory;
import org.eclipse.wst.server.core.internal.ServerPlugin;

public class ModuleFactoriesExtensionTestCase extends PerformanceTestCase {
	public void testModuleFactoriesExtension() throws Exception {
		Dimension[] dims = new Dimension[] {Dimension.ELAPSED_PROCESS, Dimension.USED_JAVA_HEAP};
		tagAsGlobalSummary("Module Factories", dims);
		startMeasuring();
		ModuleFactory[] factories = ServerPlugin.getModuleFactories();
		if (factories != null) {
			for (ModuleFactory factory : factories)
				factory.getModules(null);
		}
		stopMeasuring();
		commitMeasurements();
		assertPerformance();
	}
}
