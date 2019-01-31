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
package org.eclipse.jst.server.core.tests;

import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.jst.server.core.tests.impl.TestRuntimeClasspathProviderDelegate;
import junit.framework.TestCase;

public class RuntimeClasspathProviderDelegateTestCase extends TestCase {
	protected static RuntimeClasspathProviderDelegate handler;

	protected RuntimeClasspathProviderDelegate getHander() {
		if (handler == null) {
			handler = new TestRuntimeClasspathProviderDelegate();
		}
		return handler;
	}

	public void testCreate() {
		getHander();
	}

	public void testResolveClasspathContainer() {
		getHander().resolveClasspathContainer(null);
	}

	public void testResolveClasspathContainer2() {
		getHander().resolveClasspathContainer(null, null);
	}

	public void testResolveClasspathContainerImpl() {
		getHander().resolveClasspathContainerImpl(null);
	}

	public void testResolveClasspathContainerImpl2() {
		getHander().resolveClasspathContainerImpl(null, null);
	}

	public void testTestAddMethods() {
		((TestRuntimeClasspathProviderDelegate) getHander()).testAddMethods();
	}

	public void testRequestClasspathContainerUpdate() {
		getHander().requestClasspathContainerUpdate(null, null);
	}

	public void testHasRuntimeClasspathChanged() {
		getHander().hasRuntimeClasspathChanged(null);
	}
}