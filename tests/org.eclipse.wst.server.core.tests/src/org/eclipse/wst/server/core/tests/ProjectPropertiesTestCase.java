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
package org.eclipse.wst.server.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.server.core.*;

public class ProjectPropertiesTestCase extends TestCase {
	protected static IProject project;
	protected static IProjectProperties props;
	
	protected static IProject projectEvent;
	protected static IServer serverEvent;
	protected static IRuntime runtimeEvent;
	protected static int count;

	protected IProjectPropertiesListener listener = new IProjectPropertiesListener() {
		public void defaultServerChanged(IProject project2, IServer server) {
			projectEvent = project2;
			serverEvent = server;
			count++;
		}

		public void runtimeTargetChanged(IProject project2, IRuntime runtime) {
			projectEvent = project2;
			runtimeEvent = runtime;
			count++;
		}
	};

	public static Test suite() {
		return new OrderedTestSuite(ProjectPropertiesTestCase.class, "AbstractServerTestCase");
	}

	public void test00GetProperties() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		if (project != null && !project.exists()) {
			project.create(null);
			project.open(null);
		}
		props = ServerCore.getProjectProperties(project);
	}

	public void test01AddListener() throws Exception {
		props.addProjectPropertiesListener(listener);
	}

	public void test02GetServer() throws Exception {
		assertNull(props.getDefaultServer());
	}

	public void test03GetRuntime() throws Exception {
		assertNull(props.getRuntimeTarget());
	}

	public void test04GetServerProject() throws Exception {
		assertFalse(props.isServerProject());
	}
	
	public void test05TestListener() throws Exception {
		assertTrue(count == 0);
	}

	public void test06SetServer() throws Exception {
		props.setDefaultServer(null, null);
	}
	
	public void test07TestListener() throws Exception {
		// no event since we didn't change it
		assertTrue(count == 0);
		count = 0;
		serverEvent = null;
	}

	public void test08SetRuntime() throws Exception {
		props.setRuntimeTarget(null, null);
	}
	
	public void test09TestListener() throws Exception {
		// no event again
		assertTrue(count == 0);
		count = 0;
		runtimeEvent = null;
	}

	public void test10SetServerProject() throws Exception {
		props.setServerProject(true, null);
		assertTrue(props.isServerProject());
	}

	public void test11UnsetServerProject() throws Exception {
		props.setServerProject(false, null);
		assertFalse(props.isServerProject());
	}
	
	public void test12TestListener() throws Exception {
		assertTrue(count == 0);
	}
	
	public void test13RemoveListener() throws Exception {
		props.removeProjectPropertiesListener(listener);
	}

	public void test14End() throws Exception {
		project.delete(true, true, null);
	}
}