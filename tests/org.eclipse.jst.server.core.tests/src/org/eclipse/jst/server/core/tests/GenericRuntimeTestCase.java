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
package org.eclipse.jst.server.core.tests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.core.GenericRuntimeUtil;
import org.eclipse.jst.server.core.internal.IGenericRuntime;
import org.eclipse.jst.server.core.internal.IGenericRuntimeWorkingCopy;
import org.eclipse.wst.server.core.*;

import junit.framework.Test;
import junit.framework.TestCase;

public class GenericRuntimeTestCase extends TestCase {
	private static final String RUNTIME_TYPE_ID = "org.eclipse.jst.server.core.runtimeType";

	protected static IRuntime runtime;
	protected static IRuntime runtimeWC;
	protected static IGenericRuntime genericRuntime;
	protected static IGenericRuntimeWorkingCopy genericRuntimeWC;

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
	
	public void test03Adapt() throws Exception {
		genericRuntime = (IGenericRuntime) runtime.getAdapter(IGenericRuntime.class);
		assertNotNull(genericRuntime);
	}
	
	public void test04Adapt() throws Exception {
		assertNotNull(runtime.getAdapter(IGenericRuntimeWorkingCopy.class));
	}
	
	public void test05GetJVM() throws Exception {
		assertNotNull(genericRuntime.getVMInstall());
	}
	
	public void test06Adapt() throws Exception {
		runtimeWC = runtime.createWorkingCopy();
		genericRuntimeWC = (IGenericRuntimeWorkingCopy) runtimeWC.getAdapter(IGenericRuntimeWorkingCopy.class);
		assertNotNull(genericRuntimeWC);
	}
	
	public void test07Adapt() throws Exception {
		assertNotNull(runtimeWC.getAdapter(IGenericRuntime.class));
	}
	
	public void test08SetJVM() throws Exception {
		assertNotNull(genericRuntimeWC.getVMInstall());
		genericRuntimeWC.setVMInstall(null);
		assertNotNull(genericRuntimeWC.getVMInstall());
	}

	public void test09DeleteRuntime() throws Exception {
		runtime.delete();
		runtime = null;
		runtimeWC = null;
		genericRuntime = null;
		genericRuntimeWC = null;
	}
}