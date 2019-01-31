/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
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
package org.eclipse.wst.server.core.tests.util;

import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IURLProvider2;
import org.eclipse.wst.server.core.util.HttpLaunchable;

public class HTTPLaunchableTestCase extends TestCase {
	protected static HttpLaunchable launch;

	protected HttpLaunchable getHttpLaunchable() {
		if (launch == null) {
			launch = new HttpLaunchable(new IURLProvider2(){
				public URL getModuleRootURL(IModule module) {
					return null;
				}
				public URL getLaunchableURL() {
					return null;
				}
			});
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