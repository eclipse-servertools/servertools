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
package org.eclipse.jst.server.core.tests;

import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.jst.server.core.tests.impl.TestRuntimeClasspathProviderDelegate;
import junit.framework.Test;
import junit.framework.TestCase;

public class RuntimeClasspathProviderDelegateTestCase extends TestCase {
	protected static RuntimeClasspathProviderDelegate handler;

	public static Test suite() {
		return new OrderedTestSuite(RuntimeClasspathProviderDelegateTestCase.class, "RuntimeClasspathProviderDelegateTestCase");
	}

	public void test00Create() {
		handler = new TestRuntimeClasspathProviderDelegate();
	}

	public void test01GetClasspathContainerLabel() {
		handler.getClasspathContainerLabel(null, null);
	}

	public void test03ResolveClasspathContainer() {
		handler.resolveClasspathContainer(null, null);
	}

	public void test04ResolveClasspathContainerImpl() {
		handler.resolveClasspathContainerImpl(null, null);
	}

	public void test05TestAddMethods() {
		((TestRuntimeClasspathProviderDelegate) handler).testAddMethods();
	}

	public void test10RequestClasspathContainerUpdate() {
		handler.requestClasspathContainerUpdate(null, null, null);
	}
}