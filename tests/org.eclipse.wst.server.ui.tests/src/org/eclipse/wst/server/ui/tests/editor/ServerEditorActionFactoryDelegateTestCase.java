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
package org.eclipse.wst.server.ui.tests.editor;

import junit.framework.TestCase;

import org.eclipse.wst.server.ui.internal.provisional.ServerEditorActionFactoryDelegate;
import org.eclipse.wst.server.ui.tests.impl.TestServerEditorActionFactoryDelegate;

/* Note: These tests may be executed in any order.  Because null is used as most
 * arguments, the order doesn't currently matter.  If non-null arguments are used,
 * it may be necessary to rewrite the tests to make them truly order independent.
 */

public class ServerEditorActionFactoryDelegateTestCase extends TestCase {
	protected static ServerEditorActionFactoryDelegate delegate;

	protected ServerEditorActionFactoryDelegate getDelegate() {
		if (delegate == null) {
			delegate = new TestServerEditorActionFactoryDelegate();
		}
		return delegate;
	}

	public void testShouldDisplay() throws Exception {
		getDelegate().shouldDisplay(null);
	}
	
	public void testCreateAction() throws Exception {
		getDelegate().createAction(null, null);
	}
}