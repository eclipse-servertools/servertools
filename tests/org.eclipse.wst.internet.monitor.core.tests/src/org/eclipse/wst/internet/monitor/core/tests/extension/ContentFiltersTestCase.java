/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core.tests.extension;

import org.eclipse.wst.internet.monitor.core.IContentFilter;
import org.eclipse.wst.internet.monitor.core.MonitorCore;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ContentFiltersTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(ContentFiltersTestCase.class, "MonitorTestCase");
	}

	public void testContentFiltersExtension() throws Exception {
		IContentFilter[] cf = MonitorCore.getContentFilters();
		if (cf != null) {
			int size = cf.length;
			for (int i = 0; i < size; i++)
				System.out.println(cf[i].getId() + " - " + cf[i].getName());
		}
	}
}