/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2004 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */
package org.eclipse.jst.server.core.tests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.core.GenericRuntimeUtil;
import org.eclipse.wst.server.core.*;

import junit.framework.Test;
import junit.framework.TestCase;

public class GenericRuntimeTestCase extends TestCase {
	private static final String RUNTIME_TYPE_ID = "org.eclipse.jst.server.core.runtimeType";

	protected static IRuntime runtime;

	public static Test suite() {
		return new OrderedTestSuite(GenericRuntimeTestCase.class, "GenericRuntimeTestCase");
	}

	public void test00CreateRuntime() throws Exception {
		IRuntimeType rt = ServerCore.findRuntimeType(RUNTIME_TYPE_ID);
		IRuntimeWorkingCopy wc = rt.createRuntime("a", null);
		wc.setLocation(new Path("c://test"));
		runtime = wc.save(false, null);
		
		assertTrue(!runtime.isWorkingCopy());
	}

	public void test01ValidateRuntime() throws Exception {
		IStatus status = runtime.validate(null);
		assertTrue(!status.isOK());
	}
	
	public void test02Util() throws Exception {
		assertTrue(GenericRuntimeUtil.isGenericJ2EERuntime(runtime));
	}

	public void test03DeleteRuntime() throws Exception {
		runtime.delete();
		runtime = null;
	}
}