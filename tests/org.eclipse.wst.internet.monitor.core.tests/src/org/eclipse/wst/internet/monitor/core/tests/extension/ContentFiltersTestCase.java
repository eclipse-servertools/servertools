/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
			int size = cf.length;
			for (int i = 0; i < size; i++)
				System.out.println(cf[i].getId() + " - " + cf[i].getName());
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