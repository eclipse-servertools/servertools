/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/

package org.eclipse.wst.server.tests.performance;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.test.performance.PerformanceTestCase;
import org.eclipse.wst.server.core.ServerUtil;

public class ModuleFactoriesExtensionTestCase extends PerformanceTestCase
{
  public static Test suite()
  {
    return new TestSuite(ModuleFactoriesExtensionTestCase.class, "ModuleFactoriesExtensionTestCase");
  }
  
  public void testModuleFactoriesExtension() throws Exception
  {
	// get modules
	startMeasuring();
	ServerUtil.getModules();
	stopMeasuring();
	commitMeasurements();
	assertPerformance();
  }
}