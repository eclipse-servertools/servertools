/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
import org.eclipse.wst.server.core.ServerCore;

public class StartupExtensionTestCase extends PerformanceTestCase {
	public void testStartupExtension() throws Exception {
		Dimension[] dims = new Dimension[] {Dimension.ELAPSED_PROCESS, Dimension.USED_JAVA_HEAP};
		tagAsGlobalSummary("Runtime/Server startup", dims);
		startMeasuring();
		ServerCore.getRuntimes();
		ServerCore.getRuntimeTypes();
		ServerCore.getServers();
		ServerCore.getServerTypes();
		stopMeasuring();
		commitMeasurements();
		assertPerformance();
	}
}
