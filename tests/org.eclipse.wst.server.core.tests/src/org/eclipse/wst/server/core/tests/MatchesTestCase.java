/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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
package org.eclipse.wst.server.core.tests;

import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;

import junit.framework.TestCase;

public class MatchesTestCase extends TestCase {
	private static final int NUM = 1000000;

	public void testMatches() {
		IRuntimeType rt = ServerCore.getRuntimeTypes()[0];
		System.out.println("Runtime: " + rt.getName());
		
		IModuleType[] mt = rt.getModuleTypes();
		for (int i = 0; i < NUM; i++) {
			ServerUtil.isSupportedModule(mt, "jst.web", "2.4");
		}
		
		long time = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			ServerUtil.isSupportedModule(mt, "jst.web", "2.4");
		}
		System.out.println("Time: " + (System.currentTimeMillis() - time));
		
		time = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			ServerUtil.isSupportedModule(mt, "jst.*", "2.*");
		}
		System.out.println("Time2: " + (System.currentTimeMillis() - time));
		
		time = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			ServerUtil.isSupportedModule(mt, "*", "2.4");
		}
		System.out.println("Time3: " + (System.currentTimeMillis() - time));
	}
}
