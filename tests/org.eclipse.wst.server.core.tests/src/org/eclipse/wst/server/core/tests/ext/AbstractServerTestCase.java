/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.tests.ext;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;

public abstract class AbstractServerTestCase extends TestCase {
	protected static IProject project;
	protected static IProjectProperties props;
	
	protected static IServer server;
	
	public static Test suite() {
		return new OrderedTestSuite(AbstractServerTestCase.class, "AbstractServerTestCase");
	}
	
	protected IServer getServer() {
		if (server == null)
			server = createServer();
		
		return server;
	}

	protected abstract IServer createServer();

	public void test00GetProperties() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		if (project != null && !project.exists()) {
			project.create(null);
			project.open(null);
		}
		props = ServerCore.getProjectProperties(project);
	}

	public void test01GetServer() throws Exception {
		assertNull(props.getDefaultServer());
	}

	public void test02SetServer() throws Exception {
		props.setDefaultServer(getServer(), null);
		assertEquals(props.getDefaultServer(), getServer());
	}

	public void test03UnSetServer() throws Exception {
		props.setDefaultServer(null, null);
		assertNull(props.getDefaultServer());
	}

	public void test04End() throws Exception {
		project.delete(true, true, null);
	}
}