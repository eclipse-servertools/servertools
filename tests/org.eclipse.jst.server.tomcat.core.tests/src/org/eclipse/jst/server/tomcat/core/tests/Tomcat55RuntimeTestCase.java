/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import junit.framework.TestSuite;

public class Tomcat55RuntimeTestCase extends AbstractTomcatRuntimeTestCase {
	protected String getRuntimeTypeId() {
		return "org.eclipse.jst.server.tomcat.runtime.55";
	}

	public static void addOrderedTests(TestSuite suite) {
		AbstractTomcatRuntimeTestCase.addOrderedTests(Tomcat55RuntimeTestCase.class, suite);
	}
}
