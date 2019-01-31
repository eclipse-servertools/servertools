/*******************************************************************************
 * Copyright (c) 2004, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatRuntime;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;

public class TomcatRuntimeTestCase extends TestCase {
	private static final String RUNTIME_TYPE_ID_32 = "org.eclipse.jst.server.tomcat.runtime.32";
	private static final String RUNTIME_TYPE_ID_40 = "org.eclipse.jst.server.tomcat.runtime.40";
	private static final String RUNTIME_TYPE_ID_41 = "org.eclipse.jst.server.tomcat.runtime.41";
	private static final String RUNTIME_TYPE_ID_50 = "org.eclipse.jst.server.tomcat.runtime.50";
	private static final String RUNTIME_TYPE_ID_55 = "org.eclipse.jst.server.tomcat.runtime.55";

	protected static IRuntime runtime;
	protected static ITomcatRuntime tomcatRuntime;

	// This test suite ensures the test methods are run in order
	public static TestSuite getOrderedTests() {
		TestSuite mySuite = new TestSuite();
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test00CreateRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test01ValidateRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test02AdaptRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test03ModifyRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test04DeleteRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test10CreateRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test11ValidateRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test12AdaptRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test13ModifyRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test14DeleteRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test20CreateRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test21ValidateRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test22AdaptRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test23ModifyRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test24DeleteRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test30CreateRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test31ValidateRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test32AdaptRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test33ModifyRuntime"));		
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test34DeleteRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test40CreateRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test41ValidateRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test42AdaptRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test43ModifyRuntime"));
		mySuite.addTest(TestSuite.createTest(TomcatRuntimeTestCase.class, "test44DeleteRuntime"));
		return mySuite;
	}	
	
	protected IRuntimeWorkingCopy createRuntime(String runtimeTypeId) throws Exception {
		IRuntimeType rt = ServerCore.findRuntimeType(runtimeTypeId);
		IRuntimeWorkingCopy wc = rt.createRuntime("a", null);
		wc.setLocation(new Path("c://test"));
		return wc;
	}

	public void test00CreateRuntime() throws Exception {
		runtime = createRuntime(RUNTIME_TYPE_ID_32).save(false, null);
		assertTrue(!runtime.isWorkingCopy());
	}
	
	protected void validateRuntime() throws Exception {
		IStatus status = runtime.validate(null);
		assertTrue(!status.isOK());
	}
	
	protected void adaptRuntime() throws Exception {
		tomcatRuntime = (ITomcatRuntime) runtime.loadAdapter(ITomcatRuntime.class, null);
		assertNotNull(tomcatRuntime);
		assertNotNull(tomcatRuntime.getVMInstall());
		assertNotNull(tomcatRuntime.getRuntimeClasspath(null));
	}
	
	protected void modifyRuntime() throws Exception {
		IRuntimeWorkingCopy wc = runtime.createWorkingCopy();
		ITomcatRuntimeWorkingCopy trwc = (ITomcatRuntimeWorkingCopy) wc.loadAdapter(ITomcatRuntimeWorkingCopy.class, null);
		trwc.setVMInstall(null);
		wc.save(true, null);
		tomcatRuntime = (ITomcatRuntime) runtime.loadAdapter(ITomcatRuntime.class, null);
		assertNotNull(tomcatRuntime.getVMInstall());
	}
	
	protected void deleteRuntime() throws Exception {
		runtime.delete();
		runtime = null;
		tomcatRuntime = null;
	}

	public void test01ValidateRuntime() throws Exception {
		validateRuntime();
	}
	
	public void test02AdaptRuntime() throws Exception {
		adaptRuntime();
	}
	
	public void test03ModifyRuntime() throws Exception {
		modifyRuntime();
	}

	public void test04DeleteRuntime() throws Exception {
		deleteRuntime();
	}
	
	public void test10CreateRuntime() throws Exception {
		runtime = createRuntime(RUNTIME_TYPE_ID_40).save(false, null);
		assertTrue(!runtime.isWorkingCopy());
	}

	public void test11ValidateRuntime() throws Exception {
		validateRuntime();
	}
	
	public void test12AdaptRuntime() throws Exception {
		adaptRuntime();
	}
	
	public void test13ModifyRuntime() throws Exception {
		modifyRuntime();
	}

	public void test14DeleteRuntime() throws Exception {
		deleteRuntime();
	}
	
	public void test20CreateRuntime() throws Exception {
		runtime = createRuntime(RUNTIME_TYPE_ID_41).save(false, null);
		assertTrue(!runtime.isWorkingCopy());
	}

	public void test21ValidateRuntime() throws Exception {
		validateRuntime();
	}
	
	public void test22AdaptRuntime() throws Exception {
		adaptRuntime();
	}
	
	public void test23ModifyRuntime() throws Exception {
		modifyRuntime();
	}

	public void test24DeleteRuntime() throws Exception {
		deleteRuntime();
	}
	
	public void test30CreateRuntime() throws Exception {
		runtime = createRuntime(RUNTIME_TYPE_ID_50).save(false, null);
		assertTrue(!runtime.isWorkingCopy());
	}

	public void test31ValidateRuntime() throws Exception {
		validateRuntime();
	}
	
	public void test32AdaptRuntime() throws Exception {
		adaptRuntime();
	}
	
	public void test33ModifyRuntime() throws Exception {
		modifyRuntime();
	}

	public void test34DeleteRuntime() throws Exception {
		deleteRuntime();
	}
	
	public void test40CreateRuntime() throws Exception {
		runtime = createRuntime(RUNTIME_TYPE_ID_55).save(false, null);
		assertTrue(!runtime.isWorkingCopy());
	}

	public void test41ValidateRuntime() throws Exception {
		validateRuntime();
	}
	
	public void test42AdaptRuntime() throws Exception {
		adaptRuntime();
	}
	
	public void test43ModifyRuntime() throws Exception {
		modifyRuntime();
	}

	public void test44DeleteRuntime() throws Exception {
		deleteRuntime();
	}
}
