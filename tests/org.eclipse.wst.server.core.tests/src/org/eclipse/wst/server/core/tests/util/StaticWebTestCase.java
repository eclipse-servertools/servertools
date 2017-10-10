/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.util;

import junit.framework.TestCase;
import org.eclipse.wst.server.core.tests.impl.TestStaticWeb;
import org.eclipse.wst.server.core.util.IStaticWeb;

public class StaticWebTestCase extends TestCase {
	protected static IStaticWeb web;

	protected IStaticWeb getStaticWeb() {
		if (web == null) {
			web = new TestStaticWeb();
		}
		return web;
	}

	public void testGetContextRoot() {
		assertNull(getStaticWeb().getContextRoot());
	}
}