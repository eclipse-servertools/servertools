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

import org.eclipse.jst.server.core.Servlet;
import junit.framework.TestCase;

public class ServletTestCase extends TestCase {
	protected static Servlet servlet;

	protected Servlet getServlet() {
		if (servlet == null) {
			servlet = new Servlet(null, "class", "alias");
		}
		return servlet;
	}

	public void testCreate() {
		getServlet();
	}
	
	public void testGetModule() {
		assertNull(getServlet().getModule());
	}
	
	public void testGetClassName() {
		assertEquals(getServlet().getServletClassName(), "class");
	}

	public void testGetAlias() {
		assertEquals(getServlet().getAlias(), "alias");
	}
	
	public void testToString() {
		getServlet().toString();
	}
}