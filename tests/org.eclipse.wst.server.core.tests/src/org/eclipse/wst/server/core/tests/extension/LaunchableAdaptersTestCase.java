/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.ILaunchableAdapter;
import org.eclipse.wst.server.core.internal.ServerPlugin;

public class LaunchableAdaptersTestCase extends TestCase {
	public void testLaunchableAdaptersExtension() throws Exception {
		ILaunchableAdapter[] la = ServerPlugin.getLaunchableAdapters();
		if (la != null) {
			int size = la.length;
			for (int i = 0; i < size; i++)
				System.out.println(la[i].getId());
		}
	}
}