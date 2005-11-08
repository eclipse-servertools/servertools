/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.tests.performance.tomcat50;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jst.server.tomcat.core.tests.module.ModuleHelper;
import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.PerformanceTestCase;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;

public class PublishTestCase extends PerformanceTestCase {
	public static Test suite() {
		return new TestSuite(PublishTestCase.class, "PublishTestCase");
	}

	public void testOpenEditor() throws Exception {
		Dimension[] dims = new Dimension[] { Dimension.ELAPSED_PROCESS, Dimension.USED_JAVA_HEAP };
		tagAsSummary("Publish to Tomcat", dims);
		
		IServer server = AbstractTomcatServerTestCase.server;
		IServerWorkingCopy wc = server.createWorkingCopy();
		
		int size = CreateModulesTestCase.NUM_MODULES;
		IModule[] modules = new IModule[size];
		for (int i = 0; i < size; i++)
			modules[i] = ModuleHelper.getModule(CreateModulesTestCase.WEB_MODULE_NAME + i);
		
		wc.modifyModules(modules, null, null);
		wc.save(true, null);
		
		startMeasuring();
		server.publish(IServer.PUBLISH_FULL, null);
		stopMeasuring();
		
		commitMeasurements();
		assertPerformance();
	}
}