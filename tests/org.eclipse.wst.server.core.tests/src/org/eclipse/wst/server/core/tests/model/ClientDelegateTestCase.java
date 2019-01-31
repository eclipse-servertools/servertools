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
package org.eclipse.wst.server.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.model.ClientDelegate;
import org.eclipse.wst.server.core.tests.impl.TestClientDelegate;

public class ClientDelegateTestCase extends TestCase {
	protected static ClientDelegate delegate;

	protected ClientDelegate getClientDelegate() {
		if (delegate == null) {
			delegate = new TestClientDelegate();
		}
		return delegate;
	}

	public void testSupports() throws Exception {
		getClientDelegate().supports(null, null, null);
	}
	
	public void testLaunch() throws Exception {
		getClientDelegate().launch(null, null, null, null);
	}
}