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

import org.eclipse.jst.server.core.IEJBModule;
import org.eclipse.jst.server.core.tests.impl.TestEJBModule;
import junit.framework.Test;
import junit.framework.TestCase;

public class EJBModuleTestCase extends TestCase {
	protected static IEJBModule module;

	public static Test suite() {
		return new OrderedTestSuite(EJBModuleTestCase.class, "EJBModuleTestCase");
	}

	public void test00Create() {
		module = new TestEJBModule();
	}

	public void test01SpecVersion() {
		module.getJ2EESpecificationVersion();
	}

	public void test04EJBSpecVersion() {
		module.getEJBSpecificationVersion();
	}
}