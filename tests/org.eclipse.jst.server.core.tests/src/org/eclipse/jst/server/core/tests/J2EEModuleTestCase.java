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

import org.eclipse.jst.server.core.tests.impl.J2EEModule;
import junit.framework.Test;
import junit.framework.TestCase;

public class J2EEModuleTestCase extends TestCase {
	protected static J2EEModule module;
	
	public static Test suite() {
		return new OrderedTestSuite(J2EEModuleTestCase.class, "J2EEModuleTestCase");
	}

	public void test00Create() {
		module = new J2EEModule();
	}
	
	public void test01SpecVersion() {
		module.getJ2EESpecificationVersion();
	}
	
	public void test02Location() {
		module.getLocation();
	}
	
	public void test03Binary() {
		module.isBinary();
	}
}