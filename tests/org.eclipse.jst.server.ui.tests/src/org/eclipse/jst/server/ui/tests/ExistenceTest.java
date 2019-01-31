/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
package org.eclipse.jst.server.ui.tests;

import org.eclipse.jst.server.ui.internal.ContextIds;
import org.eclipse.jst.server.ui.internal.JavaServerUIPlugin;
import junit.framework.TestCase;

public class ExistenceTest extends TestCase {
	public void testPluginExists() {
		assertNotNull(JavaServerUIPlugin.getInstance());
	}

	public void testContextExists() {
		assertNotNull(ContextIds.RUNTIME_COMPOSITE);
	}
}