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

import junit.framework.TestCase;

import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.eclipse.wst.server.core.tests.impl.TestRuntimeDelegate;

public class RuntimeDelegateTestCase extends TestCase {
	protected static RuntimeDelegate delegate;

	public void test00CreateDelegate() throws Exception {
		delegate = new TestRuntimeDelegate();
	}

	public void test03GetRuntime() throws Exception {
		delegate.getRuntime();
	}
	
	public void test04GetRuntimeWorkingCopy() throws Exception {
		delegate.getRuntimeWorkingCopy();
	}
	
	public void test05Validate() {
		try {
			delegate.validate();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test11Dispose() throws Exception {
		delegate.dispose();
	}
	
	public void test12SetDefaults() throws Exception {
		delegate.setDefaults(null);
	}
	
	public void test13Protected() throws Exception {
		((TestRuntimeDelegate)delegate).testProtected();
	}
}