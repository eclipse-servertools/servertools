/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.model;

import org.eclipse.wst.server.core.model.IParentModuleWithChild;
import org.eclipse.wst.server.core.tests.impl.TestParentWithChildApplication;

import junit.framework.TestCase;

public class ParentModuleWithChildTestCase extends TestCase {
	protected static IParentModuleWithChild parentApp;

	public void test00Create() {
		parentApp = new TestParentWithChildApplication();
	}

	public void test03Modules() {
		parentApp.getModules();
	}

	public void test04URI() {
		parentApp.getPath(null);
	}
}