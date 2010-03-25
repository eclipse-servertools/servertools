/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.ext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ProjectProperties;
import org.eclipse.wst.server.core.internal.Runtime;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
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
	protected static ProjectProperties props;

	protected static IRuntime runtime;
	protected static IRuntimeWorkingCopy runtimeWC;
	
	private static final PropertyChangeListener pcl = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent arg0) {
			// ignore
		}
	};

	protected IRuntime getRuntime() throws Exception {
		if (runtime == null)
			runtime = createRuntime();
		
		return runtime;
	}

	protected IProject getProject() throws CoreException {
		if (project == null) {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
			if (project != null && !project.exists()) {
				project.create(null);
				project.open(null);
			}
		}
		return project;
	}

	protected ProjectProperties getProjectProperties() throws CoreException {
		if (props == null) {
			props = ServerPlugin.getProjectProperties(getProject());
		}
		return props;
	}

	protected IRuntimeWorkingCopy getRuntimeWorkingCopy() throws Exception {
		if (runtimeWC == null) {
			runtimeWC = getRuntime().createWorkingCopy();
		}
		return runtimeWC;
	}

	public abstract IRuntime createRuntime() throws Exception;
	
	public abstract void deleteRuntime(IRuntime runtime2) throws Exception;

	public static void addOrderedTests(Class<? extends TestCase> testClass, TestSuite suite) {
		suite.addTest(TestSuite.createTest(testClass, "deleteProject"));
		suite.addTest(TestSuite.createTest(testClass, "createWorkingCopy"));
		suite.addTest(TestSuite.createTest(testClass, "isWorkingCopyDirty"));
		suite.addTest(TestSuite.createTest(testClass, "setReadOnly"));
		suite.addTest(TestSuite.createTest(testClass, "setStub"));
		suite.addTest(TestSuite.createTest(testClass, "isWorkingCopyDirty2"));
		suite.addTest(TestSuite.createTest(testClass, "addPropertyChangeListener"));
		suite.addTest(TestSuite.createTest(testClass, "removePropertyChangeListener"));
	}

	public static void addFinalTests(Class<? extends TestCase> testClass,TestSuite suite) {
		suite.addTest(TestSuite.createTest(testClass, "clearWorkingCopy"));
		suite.addTest(TestSuite.createTest(testClass, "deleteRuntime"));
	}

	public void testGetProperties() throws Exception {
		getProjectProperties();
	}

	public void testGetRuntime() throws Exception {
		assertNull(getProjectProperties().getRuntimeTarget());
	}

	public void deleteProject() throws Exception {
		getProject().delete(true, true, null);
	}

	public void testGetAdapter() throws Exception {
		getRuntime().getAdapter(RuntimeDelegate.class);
	}

	public void testLoadAdapter() throws Exception {
		getRuntime().loadAdapter(RuntimeDelegate.class, null);
	}

	public void testValidate() throws Exception {
		IStatus status = getRuntime().validate(null);
		assertTrue(status.isOK() || status.getSeverity() == IStatus.WARNING);
	}

	public void testValidate2() throws Exception {
		IRuntimeWorkingCopy wc = getRuntime().createWorkingCopy();
		wc.setLocation(null);
		IStatus status = wc.validate(null);
		assertTrue(!status.isOK());
	}

	public void testModifyRuntime() throws Exception {
		IRuntimeWorkingCopy wc = getRuntime().createWorkingCopy();
		String name = wc.getName();
		wc.setName(name + "x");
		wc.setName(name);
		wc.save(false, null);
	}

	public void testIsPrivate() throws Exception {
		((Runtime)getRuntime()).isPrivate();
	}
	
	public void testIsReadOnly() throws Exception {
		getRuntime().isReadOnly();
	}

	public void testGetId() throws Exception {
		getRuntime().getId();
	}

	public void testGetName() throws Exception {
		getRuntime().getName();
	}

	public void testGetTimestamp() throws Exception {
		((Runtime)getRuntime()).getTimestamp();
	}
	
	public void testGetRuntimeType() throws Exception {
		assertNotNull(getRuntime().getRuntimeType());
	}
	
	public void testGetLocation() throws Exception {
		assertNotNull(getRuntime().getLocation());
	}
	
	public void testIsStub() throws Exception {
		getRuntime().isStub();
	}

	public void createWorkingCopy() throws Exception {
		getRuntimeWorkingCopy();
	}

	public void isWorkingCopyDirty() throws Exception {
		assertFalse(getRuntimeWorkingCopy().isDirty());
	}

	public void setReadOnly() throws Exception {
		getRuntimeWorkingCopy().setReadOnly(true);
		getRuntimeWorkingCopy().setReadOnly(false);
	}

	public void setStub() throws Exception {
		getRuntimeWorkingCopy().setStub(true);
		getRuntimeWorkingCopy().setStub(false);
	}

	public void isWorkingCopyDirty2() throws Exception {
		assertTrue(getRuntimeWorkingCopy().isDirty());
	}

	public void addPropertyChangeListener() throws Exception {
		getRuntimeWorkingCopy().addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener() throws Exception {
		getRuntimeWorkingCopy().removePropertyChangeListener(pcl);
	}

	public void clearWorkingCopy() {
		runtimeWC = null;
	}

	public void deleteRuntime() throws Exception {
		deleteRuntime(getRuntime());
		runtime = null;
	}
}