/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.model;

import java.util.ArrayList;
import java.util.HashMap;
import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.Runtime;
import org.eclipse.wst.server.core.internal.RuntimeWorkingCopy;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.tests.impl.TestRuntimeDelegate;

public class RuntimeDelegateTestCase extends TestCase {
	protected static RuntimeDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(RuntimeDelegateTestCase.class, "RuntimeDelegateTestCase");
	}

	public void test00CreateDelegate() throws Exception {
		delegate = new TestRuntimeDelegate();
	}
	
	public void test01Initialize() throws Exception {
		delegate.initialize();
	}
	
	public void test02Initialize() throws Exception {
		delegate.initialize(new RuntimeWorkingCopy(new Runtime(null)));
	}

	public void test03GetRuntime() throws Exception {
		delegate.getRuntime();
	}
	
	public void test04GetRuntimeWorkingCopy() throws Exception {
		delegate.getRuntimeWorkingCopy();
	}
	
	public void test05Validate() throws Exception {
		delegate.validate();
	}

	public void test06GetAttribute() throws Exception {
		delegate.getAttribute("test", false);
	}
	
	public void test07GetAttribute() throws Exception {
		delegate.getAttribute("test", 0);
	}
	
	public void test08GetAttribute() throws Exception {
		delegate.getAttribute("test", new ArrayList());
	}
	
	public void test09GetAttribute() throws Exception {
		delegate.getAttribute("test", new HashMap());
	}
	
	public void test10GetAttribute() throws Exception {
		delegate.getAttribute("test", "test");
	}
	
	public void test11Dispose() throws Exception {
		delegate.dispose();
	}
	
	public void test12SetDefaults() throws Exception {
		delegate.setDefaults();
	}
	
	public void test13SetAttribute() throws Exception {
		delegate.setAttribute("test", false);
	}
	
	public void test14SetAttribute() throws Exception {
		delegate.setAttribute("test", 0);
	}
	
	public void test15SetAttribute() throws Exception {
		delegate.setAttribute("test", new ArrayList());
	}
	
	public void test16SetAttribute() throws Exception {
		delegate.setAttribute("test", new HashMap());
	}
	
	public void test17SetAttribute() throws Exception {
		delegate.setAttribute("test", "test");
	}
}