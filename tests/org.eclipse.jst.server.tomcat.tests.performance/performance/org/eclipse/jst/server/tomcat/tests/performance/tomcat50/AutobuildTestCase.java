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

import org.eclipse.core.resources.ResourcesPlugin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AutobuildTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(AutobuildTestCase.class, "AutobuildTestCase");
	}

	public void testBuild() throws Exception {
		ResourcesPlugin.getWorkspace().getDescription().setAutoBuilding(true);
	}
}