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

import org.eclipse.jst.server.core.IConnectorModule;
import org.eclipse.jst.server.core.tests.impl.TestConnectorModule;
import junit.framework.TestCase;

public class ConnectorModuleTestCase extends TestCase {
	protected static IConnectorModule module;

	protected IConnectorModule getConnectorModule() {
		if (module == null) {
			module = new TestConnectorModule();
		}
		return module;
	}

	public void testCreate() {
		getConnectorModule();
	}

	public void testClasspath() {
		getConnectorModule().getClasspath();
	}
}