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

import org.eclipse.jst.server.core.IEnterpriseApplication;
import org.eclipse.jst.server.core.tests.impl.TestEnterpriseApplication;

import junit.framework.TestCase;

public class EnterpriseApplicationTestCase extends TestCase {
	protected static IEnterpriseApplication app;

	public void test00Create() {
		app = new TestEnterpriseApplication();
	}

	public void test03Modules() {
		app.getModules();
	}

	public void test04URI() {
		app.getURI(null);
	}
}