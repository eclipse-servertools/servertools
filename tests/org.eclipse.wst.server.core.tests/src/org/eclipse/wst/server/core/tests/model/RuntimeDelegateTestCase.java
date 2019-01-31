/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
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
package org.eclipse.wst.server.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.eclipse.wst.server.core.tests.impl.TestRuntimeDelegate;

public class RuntimeDelegateTestCase extends TestCase {
	protected static RuntimeDelegate delegate;
	
	protected RuntimeDelegate getRuntimeDelegate() {
		if (delegate == null) {
			delegate = new TestRuntimeDelegate();
		}
		return delegate;
	}

	public void testGetRuntime() throws Exception {
		getRuntimeDelegate().getRuntime();
	}
	
	public void testGetRuntimeWorkingCopy() throws Exception {
		getRuntimeDelegate().getRuntimeWorkingCopy();
	}
	
	public void testValidate() {
		try {
			getRuntimeDelegate().validate();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void testDispose() throws Exception {
		getRuntimeDelegate().dispose();
	}
	
	public void testSetDefaults() throws Exception {
		getRuntimeDelegate().setDefaults(null);
	}
	
	public void testProtected() throws Exception {
		((TestRuntimeDelegate)getRuntimeDelegate()).testProtected();
	}
}