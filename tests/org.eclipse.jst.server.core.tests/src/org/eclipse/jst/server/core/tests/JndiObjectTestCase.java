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

import org.eclipse.jst.server.core.JndiObject;
import junit.framework.Test;
import junit.framework.TestCase;

public class JndiObjectTestCase extends TestCase {
	protected static JndiObject obj;
	
	public static Test suite() {
		return new OrderedTestSuite(JndiObjectTestCase.class, "JndiObjectTestCase");
	}

	public void test00Create() {
		obj = new JndiObject(null, "test");
	}
	
	public void test01GetModule() {
		assertNull(obj.getModule());
	}
	
	public void test02GetJNDIName() {
		assertEquals(obj.getJndiName(), "test");
	}
}