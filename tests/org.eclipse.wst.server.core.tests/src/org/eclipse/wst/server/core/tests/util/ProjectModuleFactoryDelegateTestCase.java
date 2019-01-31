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
package org.eclipse.wst.server.core.tests.util;

import junit.framework.TestCase;
import org.eclipse.wst.server.core.tests.impl.TestProjectModuleFactoryDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;

public class ProjectModuleFactoryDelegateTestCase extends TestCase {
	protected static ProjectModuleFactoryDelegate delegate;

	protected ProjectModuleFactoryDelegate getProjectModuleFactoryDelegate() {
		if (delegate == null) {
			delegate = new TestProjectModuleFactoryDelegate();
		}
		return delegate;
	}

	public void testGetModuleDelegate() {
		getProjectModuleFactoryDelegate().getModuleDelegate(null);
	}

	public void testGetModules() {
		getProjectModuleFactoryDelegate().getModules();
	}

	public void testTestProtected() throws Exception {
		((TestProjectModuleFactoryDelegate)getProjectModuleFactoryDelegate()).testProtected();
	}
}