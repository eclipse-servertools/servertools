/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.tests.ext;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;
/**
 * Abstract runtime test case. Use this harness to test a specific runtime.
 * All you have to do is extend this class, implement the abstract
 * method(s) and add the test case to your suite.
 * <p>
 * You are welcome to add type-specific tests to this method. The test
 * method numbers (i.e. the XX in testXX()) should be between 200 and 1000.
 * </p>
 */
public abstract class AbstractRuntimeTestCase extends TestCase {
	protected static IProject project;
	protected static IProjectProperties props;

	protected static IRuntime runtime;

	public static Test suite() {
		return new OrderedTestSuite(AbstractRuntimeTestCase.class, "AbstractRuntimeTestCase");
	}

	protected IRuntime getRuntime() throws Exception {
		if (runtime == null)
			runtime = createRuntime();
		
		return runtime;
	}

	public abstract IRuntime createRuntime() throws Exception;
	
	public abstract void deleteRuntime(IRuntime runtime2) throws Exception;

	public void test0000GetProperties() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		if (project != null && !project.exists()) {
			project.create(null);
			project.open(null);
		}
		props = ServerCore.getProjectProperties(project);
	}

	public void test0001GetRuntime() throws Exception {
		assertNull(props.getRuntimeTarget());
	}

	public void test0002SetRuntime() throws Exception {
		props.setRuntimeTarget(getRuntime(), null);
		assertEquals(props.getRuntimeTarget(), getRuntime());
	}

	public void test0003UnsetRuntime() throws Exception {
		props.setRuntimeTarget(null, null);
		assertNull(props.getRuntimeTarget());
	}

	public void test0004End() throws Exception {
		project.delete(true, true, null);
	}

	public void test0005Delegate() throws Exception {
		getRuntime().getAdapter(RuntimeDelegate.class);
	}

	public void test0006Validate() throws Exception {
		IStatus status = getRuntime().validate(null);
		assertTrue(status.isOK() || status.getSeverity() == IStatus.WARNING);
	}
	
	public void test0007Validate() throws Exception {
		IRuntimeWorkingCopy wc = getRuntime().createWorkingCopy();
		wc.setLocation(null);
		IStatus status = wc.validate(null);
		assertTrue(!status.isOK());
	}

	public void test0008ModifyRuntime() throws Exception {
		IRuntimeWorkingCopy wc = getRuntime().createWorkingCopy();
		wc.setName(wc.getName() + "x");
		wc.save(false, null);
	}

	public void test1001Delete() throws Exception {
		deleteRuntime(getRuntime());
		runtime = null;
	}
}