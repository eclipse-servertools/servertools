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

import org.eclipse.jst.server.core.IApplicationClientModule;
import org.eclipse.jst.server.core.tests.impl.TestApplicationClientModule;
import junit.framework.Test;
import junit.framework.TestCase;

public class ApplicationClientTestCase extends TestCase {
	protected static IApplicationClientModule module;

	public static Test suite() {
		return new OrderedTestSuite(ApplicationClientTestCase.class, "ApplicationClientTestCase");
	}

	public void test00Create() {
		module = new TestApplicationClientModule();
	}
}