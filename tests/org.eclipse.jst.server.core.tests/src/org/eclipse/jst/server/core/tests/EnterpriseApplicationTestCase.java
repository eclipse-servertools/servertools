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
package org.eclipse.jst.server.core.tests;

import org.eclipse.jst.server.core.IEnterpriseApplication;
import org.eclipse.jst.server.core.tests.impl.TestEnterpriseApplication;

import junit.framework.TestCase;

public class EnterpriseApplicationTestCase extends TestCase {
	protected static IEnterpriseApplication app;

	protected IEnterpriseApplication getEnterpriseApplication() {
		if (app == null) {
			app = new TestEnterpriseApplication();
		}
		return app;
	}

	public void testCreate() {
		getEnterpriseApplication();
	}

	public void testModules() {
		getEnterpriseApplication().getModules();
	}

	public void testURI() {
		getEnterpriseApplication().getURI(null);
	}
}