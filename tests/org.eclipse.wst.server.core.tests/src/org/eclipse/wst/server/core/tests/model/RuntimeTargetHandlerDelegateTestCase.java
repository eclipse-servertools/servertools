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

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.wst.server.core.model.RuntimeTargetHandlerDelegate;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.tests.impl.TestRuntimeTargetHandlerDelegate;

public class RuntimeTargetHandlerDelegateTestCase extends TestCase {
	protected static RuntimeTargetHandlerDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(RuntimeTargetHandlerDelegateTestCase.class, "RuntimeTargetHandlerDelegateTestCase");
	}

	public void test00CreateDelegate() throws Exception {
		delegate = new TestRuntimeTargetHandlerDelegate();
	}
	
	public void test01Initialize() throws Exception {
		delegate.initialize(null);
	}
	
	public void test02GetRuntimeTargetHandler() throws Exception {
		delegate.getRuntimeTargetHandler();
	}
	
	public void test03SetRuntimeTarget() throws Exception {
		delegate.setRuntimeTarget(null, null, null);
	}
	
	public void test04RemoveRuntimeTarget() throws Exception {
		delegate.removeRuntimeTarget(null, null, null);
	}
}