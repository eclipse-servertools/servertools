/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.server.tomcat.core.internal.Tomcat41Configuration;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.wst.server.core.IModule;

/**
 *
 */
public class Tomcat41ServerTestCase extends AbstractTomcatServerTestCase {
	protected String getServerTypeId() {
		return "org.eclipse.jst.server.tomcat.41";
	}

	protected void verifyPublishedModule(IPath baseDir, IModule module)
			throws Exception {
		Tomcat41TestConfiguration config = new Tomcat41TestConfiguration(null);
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

class Tomcat41TestConfiguration extends  Tomcat41Configuration {
	/**
	 * @param path
	 */
	public Tomcat41TestConfiguration(IFolder path) {
		super(path);
	}

	/**
	 * @return server instance
	 */
	public ServerInstance getServerInstance() {
		return serverInstance;
	}
}
