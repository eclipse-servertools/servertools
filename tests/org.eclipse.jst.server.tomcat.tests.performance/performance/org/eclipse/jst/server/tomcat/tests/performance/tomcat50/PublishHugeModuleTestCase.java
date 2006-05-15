/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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

public class PublishHugeModuleTestCase extends PerformanceTestCase {
	public static Test suite() {
		return new TestSuite(PublishHugeModuleTestCase.class, "PublishHugeModuleTestCase");
	}

	public void testHugePublish() throws Exception {
		Dimension[] dims = new Dimension[] { Dimension.ELAPSED_PROCESS, Dimension.USED_JAVA_HEAP };
		tagAsSummary("Publish huge module to Tomcat", dims);
		
		IServer server = AbstractTomcatServerTestCase.server;
		IServerWorkingCopy wc = server.createWorkingCopy();
		
		// remove previous apps
		IModule[] remove = server.getModules();
		wc.modifyModules(null, remove, null);
		wc.save(true, null);
		server.publish(IServer.PUBLISH_FULL, null);
		
		for (int i = 0; i < 5; i++) {
			// add huge app
			IModule[] add = new IModule[1];
			add[0] = ModuleHelper.getModule(CreateHugeModuleTestCase.WEB_MODULE_NAME);
			wc.modifyModules(add, null, null);
			wc.save(true, null);
			
			// publish huge app
			startMeasuring();
			server.publish(IServer.PUBLISH_FULL, null);
			stopMeasuring();
			
			// remove huge app and republish
			remove = server.getModules();
			wc.modifyModules(null, add, null);
			wc.save(true, null);
			server.publish(IServer.PUBLISH_FULL, null);
		}
		
		commitMeasurements();
		assertPerformance();
	}
}