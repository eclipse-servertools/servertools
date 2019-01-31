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
package org.eclipse.wst.internet.monitor.core.tests.extension;

import org.eclipse.wst.internet.monitor.core.internal.IContentFilter;
import org.eclipse.wst.internet.monitor.core.internal.MonitorPlugin;
import junit.framework.TestCase;


public class ContentFiltersTestCase extends TestCase {
	public void test1ContentFiltersExtension() throws Exception {
		IContentFilter[] cf = MonitorPlugin.getInstance().getContentFilters();
		if (cf != null) {
			for (IContentFilter c : cf)
				System.out.println(c.getId() + " - " + c.getName());
		}
	}
	
	public void test2ContentFiltersExtension() throws Exception {
		try {
			MonitorPlugin.getInstance().findContentFilter(null);
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test3ContentFiltersExtension() throws Exception {
		IContentFilter cf = MonitorPlugin.getInstance().findContentFilter("abc.xyz");
		assertTrue(cf == null);
	}
}
