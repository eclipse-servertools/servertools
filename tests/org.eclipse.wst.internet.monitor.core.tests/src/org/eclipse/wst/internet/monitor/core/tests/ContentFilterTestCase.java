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
package org.eclipse.wst.internet.monitor.core.tests;

import org.eclipse.wst.internet.monitor.core.internal.provisional.ContentFilterDelegate;

import junit.framework.TestCase;

public class ContentFilterTestCase extends TestCase {
	protected static ContentFilterDelegate delegate;

	protected ContentFilterDelegate getContentFilterDelegate() {
		if (delegate == null) {
			delegate = new TestContentFilterDelegate();
		}
		return delegate;
	}

	public void testCreation() {
		getContentFilterDelegate();
	}
	
	public void testFilter() throws Exception {
		getContentFilterDelegate().filter(null, false, null);
	}
}
