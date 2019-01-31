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
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.IPublishTask;
import org.eclipse.wst.server.core.internal.ServerPlugin;

public class ServerTasksTestCase extends TestCase {
	public void testServerTasksExtension() throws Exception {
		IPublishTask[] serverTasks = ServerPlugin.getPublishTasks();
		if (serverTasks != null) {
			for (IPublishTask task : serverTasks)
				System.out.println(task.getId());
		}
	}
}