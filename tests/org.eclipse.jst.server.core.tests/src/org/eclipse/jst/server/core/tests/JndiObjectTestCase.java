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

import org.eclipse.jst.server.core.JndiObject;
import junit.framework.TestCase;

public class JndiObjectTestCase extends TestCase {
	protected static JndiObject obj;

	protected JndiObject getJndiObject() {
		if (obj == null) {
			obj = new JndiObject(null, "test");
		}
		return obj;
	}

	public void testCreate() {
		getJndiObject();
	}
	
	public void testGetModule() {
		assertNull(getJndiObject().getModule());
	}
	
	public void testGetJNDIName() {
		assertEquals(getJndiObject().getJndiName(), "test");
	}
}