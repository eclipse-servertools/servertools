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

import org.eclipse.wst.internet.monitor.core.IProtocolAdapter;
import org.eclipse.wst.internet.monitor.core.MonitorCore;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ProtocolAdaptersTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(ProtocolAdaptersTestCase.class, "ProtocolAdaptersTestCase");
	}

	public void testProtocolAdaptersExtension() throws Exception {
		IProtocolAdapter[] pa = MonitorCore.getProtocolAdapters();
		if (pa != null) {
			int size = pa.length;
			for (int i = 0; i < size; i++)
				System.out.println(pa[i].getId() + " - " + pa[i].getName());
		}
	}
}