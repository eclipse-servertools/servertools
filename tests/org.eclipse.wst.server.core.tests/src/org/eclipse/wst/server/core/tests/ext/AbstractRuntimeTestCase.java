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

public abstract class AbstractRuntimeTestCase extends TestCase {
	protected static IProject project;
	protected static IProjectProperties props;

	protected static IRuntime runtime;

	public static Test suite() {
		return new OrderedTestSuite(AbstractRuntimeTestCase.class, "AbstractRuntimeTestCase");
	}

	protected IRuntime getRuntime() {
		if (runtime == null)
			runtime = createRuntime();
		
		return runtime;
	}

	protected abstract IRuntime createRuntime();

	public void test00GetProperties() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		if (project != null && !project.exists()) {
			project.create(null);
			project.open(null);
		}
		props = ServerCore.getProjectProperties(project);
	}

	public void test01GetRuntime() throws Exception {
		assertNull(props.getRuntimeTarget());
	}

	public void test02SetRuntime() throws Exception {
		props.setRuntimeTarget(getRuntime(), null);
		assertEquals(props.getRuntimeTarget(), getRuntime());
	}

	public void test03UnsetRuntime() throws Exception {
		props.setRuntimeTarget(null, null);
		assertNull(props.getRuntimeTarget());
	}

	public void test04End() throws Exception {
		project.delete(true, true, null);
	}
}