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
package org.eclipse.wst.server.core.tests.util;

import junit.framework.TestCase;
import org.eclipse.wst.server.core.tests.impl.TestStaticWeb;
import org.eclipse.wst.server.core.util.IStaticWeb;

public class StaticWebTestCase extends TestCase {
	protected static IStaticWeb web;

	public void test00Create() {
		web = new TestStaticWeb();
	}
	
	public void test01GetContextRoot() {
		assertNull(web.getContextRoot());
	}
}