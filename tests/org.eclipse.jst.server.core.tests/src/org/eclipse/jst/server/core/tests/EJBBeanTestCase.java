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

import org.eclipse.jst.server.core.EJBBean;
import junit.framework.TestCase;

public class EJBBeanTestCase extends TestCase {
	protected static EJBBean bean;

	public void test00Create() {
		bean = new EJBBean(null, "test", false, true);
	}
	
	public void test01GetModule() {
		assertNull(bean.getModule());
	}
	
	public void test02GetJNDIName() {
		assertEquals(bean.getJndiName(), "test");
	}

	public void test03Local() {
		assertTrue(bean.hasLocalInterface());
	}
	
	public void test04Remote() {
		assertFalse(bean.hasRemoteInterface());
	}
}