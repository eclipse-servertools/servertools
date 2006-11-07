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

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.server.core.internal.ProjectProperties;
import org.eclipse.wst.server.core.internal.ServerPlugin;

public class ProjectPropertiesTestCase extends TestCase {
	protected static IProject project;
	protected static ProjectProperties props;

	public void test00GetProperties() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		if (project != null && !project.exists()) {
			project.create(null);
			project.open(null);
		}
		props = ServerPlugin.getProjectProperties(project);
	}

	public void test03GetRuntime() throws Exception {
		assertNull(props.getRuntimeTarget());
	}

	public void test04GetServerProject() throws Exception {
		assertFalse(props.isServerProject());
	}

	public void test10SetServerProject() throws Exception {
		props.setServerProject(true, null);
		assertTrue(props.isServerProject());
	}

	public void test11UnsetServerProject() throws Exception {
		props.setServerProject(false, null);
		assertFalse(props.isServerProject());
	}

	public void test14End() throws Exception {
		project.delete(true, true, null);
	}
}