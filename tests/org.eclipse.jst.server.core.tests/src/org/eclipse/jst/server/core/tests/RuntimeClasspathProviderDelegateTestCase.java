/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
import junit.framework.TestCase;

public class RuntimeClasspathProviderDelegateTestCase extends TestCase {
	protected static RuntimeClasspathProviderDelegate handler;

	public void test00Create() {
		handler = new TestRuntimeClasspathProviderDelegate();
	}

	public void test01ResolveClasspathContainer() {
		handler.resolveClasspathContainer(null);
	}

	public void test02ResolveClasspathContainer() {
		handler.resolveClasspathContainer(null, null);
	}

	public void test03ResolveClasspathContainerImpl() {
		handler.resolveClasspathContainerImpl(null);
	}

	public void test04ResolveClasspathContainerImpl() {
		handler.resolveClasspathContainerImpl(null, null);
	}

	public void test05TestAddMethods() {
		((TestRuntimeClasspathProviderDelegate) handler).testAddMethods();
	}

	public void test06RequestClasspathContainerUpdate() {
		handler.requestClasspathContainerUpdate(null, null);
	}

	public void test07HasRuntimeClasspathChanged() {
		handler.hasRuntimeClasspathChanged(null);
	}
}