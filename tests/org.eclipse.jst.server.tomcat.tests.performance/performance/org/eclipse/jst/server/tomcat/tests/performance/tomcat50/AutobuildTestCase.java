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

import org.eclipse.core.resources.ResourcesPlugin;

import junit.framework.TestCase;

public class AutobuildTestCase extends TestCase {
	public void testBuild() throws Exception {
		ResourcesPlugin.getWorkspace().getDescription().setAutoBuilding(true);
	}
}
