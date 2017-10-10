/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import junit.framework.TestSuite;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.server.tomcat.core.internal.Tomcat60Configuration;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.wst.server.core.IModule;

/**
 *
 */
public class Tomcat60ServerTestCase extends AbstractTomcatServerTestCase {
	protected String getServerTypeId() {
		return "org.eclipse.jst.server.tomcat.60";
	}

	public static void addOrderedTests(TestSuite suite) {
		AbstractTomcatServerTestCase.addOrderedTests(Tomcat60ServerTestCase.class, suite);
	}

	protected void verifyPublishedModule(IPath baseDir, IModule module)
			throws Exception {
		Tomcat60TestConfiguration config = new Tomcat60TestConfiguration(null);
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

class Tomcat60TestConfiguration extends  Tomcat60Configuration {
	/**
	 * @param path
	 */
	public Tomcat60TestConfiguration(IFolder path) {
		super(path);
	}

	/**
	 * @return server instance
	 */
	public ServerInstance getServerInstance() {
		return serverInstance;
	}
}
