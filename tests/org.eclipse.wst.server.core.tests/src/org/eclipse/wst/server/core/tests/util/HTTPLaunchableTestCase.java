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
package org.eclipse.wst.server.core.tests.util;

import junit.framework.TestCase;
import org.eclipse.wst.server.core.util.HttpLaunchable;

public class HTTPLaunchableTestCase extends TestCase {
	protected static HttpLaunchable launch;

	protected HttpLaunchable getHttpLaunchable() {
		if (launch == null) {
			launch = new HttpLaunchable(null);
		}
		return launch;
	}

	public void testGetURL() {
		assertNull(getHttpLaunchable().getURL());
	}
	
	public void testToString() {
		try {
			getHttpLaunchable().toString();
		} catch (Exception e) {
			// ignore
		}
	}
}