/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.tests;

import org.eclipse.jst.server.core.Servlet;
import junit.framework.TestCase;

public class ServletTestCase extends TestCase {
	protected static Servlet servlet;

	public void test00Create() {
		servlet = new Servlet(null, "class", "alias");
	}
	
	public void test01GetModule() {
		assertNull(servlet.getModule());
	}
	
	public void test02GetClassName() {
		assertEquals(servlet.getServletClassName(), "class");
	}

	public void test02GetAlias() {
		assertEquals(servlet.getAlias(), "alias");
	}
	
	public void test03ToString() {
		servlet.toString();
	}
}