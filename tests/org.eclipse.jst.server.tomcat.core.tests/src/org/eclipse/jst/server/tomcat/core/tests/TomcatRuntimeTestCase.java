/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2004 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */
package org.eclipse.jst.server.tomcat.core.tests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.*;

import junit.framework.Test;
import junit.framework.TestCase;

public class TomcatRuntimeTestCase extends TestCase {
	private static final String RUNTIME_TYPE_ID_32 = "org.eclipse.jst.server.tomcat.runtime.32";
	private static final String RUNTIME_TYPE_ID_40 = "org.eclipse.jst.server.tomcat.runtime.40";
	private static final String RUNTIME_TYPE_ID_41 = "org.eclipse.jst.server.tomcat.runtime.41";
	private static final String RUNTIME_TYPE_ID_50 = "org.eclipse.jst.server.tomcat.runtime.50";
	private static final String RUNTIME_TYPE_ID_55 = "org.eclipse.jst.server.tomcat.runtime.55";

	protected static IRuntime runtime;

	public static Test suite() {
		return new OrderedTestSuite(TomcatRuntimeTestCase.class, "TomcatRuntimeTestCase");
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

	public void test01ValidateRuntime() throws Exception {
		IStatus status = runtime.validate(null);
		assertTrue(!status.isOK());
	}

	public void test02DeleteRuntime() throws Exception {
		runtime.delete();
		runtime = null;
	}
	
	public void test10CreateRuntime() throws Exception {
		runtime = createRuntime(RUNTIME_TYPE_ID_40).save(false, null);
		assertTrue(!runtime.isWorkingCopy());
	}

	public void test11ValidateRuntime() throws Exception {
		IStatus status = runtime.validate(null);
		assertTrue(!status.isOK());
	}

	public void test12DeleteRuntime() throws Exception {
		runtime.delete();
		runtime = null;
	}
	
	public void test20CreateRuntime() throws Exception {
		runtime = createRuntime(RUNTIME_TYPE_ID_41).save(false, null);
		assertTrue(!runtime.isWorkingCopy());
	}

	public void test21ValidateRuntime() throws Exception {
		IStatus status = runtime.validate(null);
		assertTrue(!status.isOK());
	}

	public void test22DeleteRuntime() throws Exception {
		runtime.delete();
		runtime = null;
	}
	
	public void test30CreateRuntime() throws Exception {
		runtime = createRuntime(RUNTIME_TYPE_ID_50).save(false, null);
		assertTrue(!runtime.isWorkingCopy());
	}

	public void test31ValidateRuntime() throws Exception {
		IStatus status = runtime.validate(null);
		assertTrue(!status.isOK());
	}

	public void test32DeleteRuntime() throws Exception {
		runtime.delete();
		runtime = null;
	}
	
	public void test40CreateRuntime() throws Exception {
		runtime = createRuntime(RUNTIME_TYPE_ID_55).save(false, null);
		assertTrue(!runtime.isWorkingCopy());
	}

	public void test41ValidateRuntime() throws Exception {
		IStatus status = runtime.validate(null);
		assertTrue(!status.isOK());
	}

	public void test42DeleteRuntime() throws Exception {
		runtime.delete();
		runtime = null;
	}
}