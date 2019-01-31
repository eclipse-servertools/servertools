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

import org.eclipse.jst.server.core.EJBBean;
import junit.framework.TestCase;

public class EJBBeanTestCase extends TestCase {
	protected static EJBBean bean;

	protected EJBBean getEJBBean() {
		if (bean == null) {
			bean = new EJBBean(null, "test", false, true);
		}
		return bean;
	}

	public void testCreate() {
		getEJBBean();
	}
	
	public void testGetModule() {
		assertNull(getEJBBean().getModule());
	}
	
	public void testGetJNDIName() {
		assertEquals(getEJBBean().getJndiName(), "test");
	}

	public void testLocal() {
		assertTrue(getEJBBean().hasLocalInterface());
	}
	
	public void testRemote() {
		assertFalse(getEJBBean().hasRemoteInterface());
	}
}