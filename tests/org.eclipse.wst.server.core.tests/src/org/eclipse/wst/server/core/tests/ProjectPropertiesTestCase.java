/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.server.core.internal.ProjectProperties;
import org.eclipse.wst.server.core.internal.ServerPlugin;

public class ProjectPropertiesTestCase extends TestCase {
	protected static IProject project;
	protected static ProjectProperties props;

	protected ProjectProperties getProjectProperties() throws Exception{
		if (props == null) {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
			if (project != null && !project.exists()) {
				project.create(null);
				project.open(null);
			}
			props = ServerPlugin.getProjectProperties(project);
		}
		return props;
	}

	public static void addOrderedTests(TestSuite suite) {
		suite.addTest(TestSuite.createTest(ProjectPropertiesTestCase.class, "deleteProject"));
	}

	public void testGetProperties() throws Exception {
		getProjectProperties();
	}

	public void testGetRuntime() throws Exception {
		assertNull(getProjectProperties().getRuntimeTarget());
	}

	public void testGetServerProject() throws Exception {
		assertFalse(getProjectProperties().isServerProject());
	}

	public void testSetServerProject() throws Exception {
		getProjectProperties().setServerProject(true, null);
		assertTrue(getProjectProperties().isServerProject());
	}

	public void testUnsetServerProject() throws Exception {
		getProjectProperties().setServerProject(false, null);
		assertFalse(getProjectProperties().isServerProject());
	}

	public void deleteProject() throws Exception {
		if (project != null) {
			project.delete(true, true, null);
		}
	}
	
}