/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
  *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.tests.performance.common;

import java.util.Iterator;
import java.util.List;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.actions.ServerAction;

public abstract class AbstractServerActionsTestCase extends ServerPerformanceTestCase {
	public void testServerActions() throws Exception {
		startMeasuring();
		IServer server = getFirstServer(getServerTypeId());
		List actions = ServerAction.getServerActions();
		for (Iterator it = actions.iterator(); it.hasNext();)
			((ServerAction)it.next()).getDelegate().supports(server);
		stopMeasuring();
		commitMeasurements();
		assertPerformance();
	}

	protected abstract String getServerTypeId();
}