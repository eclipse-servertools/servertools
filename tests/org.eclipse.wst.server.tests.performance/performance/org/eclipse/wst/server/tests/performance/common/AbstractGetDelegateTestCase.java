/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.tests.performance.common;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerDelegate;

public abstract class AbstractGetDelegateTestCase extends ServerPerformanceTestCase {
	protected void setUp() throws Exception {
		super.setUp();
		closeIntro();
	}

	public void testGetDelegate() throws Exception {
		startMeasuring();
		createRuntime(getRuntimeTypeId(), getRuntimeTypeLocation());
		IServer server = createServer(getServerTypeId());
		server.getAdapter(ServerDelegate.class);
		stopMeasuring();
		commitMeasurements();
		assertPerformance();
	}
  
	protected abstract String getRuntimeTypeId();
	protected abstract String getRuntimeTypeLocation();
	protected abstract String getServerTypeId();
}