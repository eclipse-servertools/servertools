/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import junit.framework.TestSuite;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.server.tomcat.core.internal.Tomcat70Configuration;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.wst.server.core.IModule;

/**
 *
 */
public class Tomcat70ServerTestCase extends AbstractTomcatServerTestCase {
	protected String getServerTypeId() {
		return "org.eclipse.jst.server.tomcat.70";
	}

	public static void addOrderedTests(TestSuite suite) {
		AbstractTomcatServerTestCase.addOrderedTests(Tomcat70ServerTestCase.class, suite);
	}

	protected void verifyPublishedModule(IPath baseDir, IModule module)
			throws Exception {
		Tomcat70TestConfiguration config = new Tomcat70TestConfiguration(null);
		config.load(baseDir.append("conf"), null);
		
		ServerInstance serverInstance = config.getServerInstance();
		Context context = serverInstance.getContext(module.getName());

		String deployDir = getTomcatServer().getDeployDirectory();
		if ("webapps".equals(deployDir)) {
			assertEquals(module.getName(), context.getDocBase());
		}
		else {
			assertEquals(getTomcatServerBehaviour().getModuleDeployDirectory(module).toOSString(), context.getDocBase());
		}
		verifyPublishedModuleFiles(module);
	}
}

class Tomcat70TestConfiguration extends  Tomcat70Configuration {
	/**
	 * @param path
	 */
	public Tomcat70TestConfiguration(IFolder path) {
		super(path);
	}

	/**
	 * @return server instance
	 */
	public ServerInstance getServerInstance() {
		return serverInstance;
	}
}
