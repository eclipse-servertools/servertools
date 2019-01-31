/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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

import org.eclipse.jst.server.core.IJ2EEModule;
import org.eclipse.jst.server.core.tests.impl.TestJ2EEModule;
import junit.framework.TestCase;

public class J2EEModuleTestCase extends TestCase {
	protected static IJ2EEModule module;

	public void test00Create() {
		module = new TestJ2EEModule();
	}
}