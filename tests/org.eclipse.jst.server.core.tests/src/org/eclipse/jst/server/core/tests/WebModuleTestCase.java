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

import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.core.tests.impl.TestWebModule;
import junit.framework.TestCase;

public class WebModuleTestCase extends TestCase {
	protected static IWebModule module;

	public void test00Create() {
		module = new TestWebModule();
	}

	public void test06ContextRoot() {
		module.getContextRoot();
	}
}