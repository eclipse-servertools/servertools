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
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.server.core.IRuntimeTargetHandler;
import org.eclipse.wst.server.core.ServerCore;

public class RuntimeTargetHandlersTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(RuntimeTargetHandlersTestCase.class, "RuntimeTargetHandlersTestCase");
	}

	public void testRuntimeTargetHandlersExtension() throws Exception {
		IRuntimeTargetHandler[] rth = ServerCore.getRuntimeTargetHandlers();
		if (rth != null) {
			int size = rth.length;
			for (int i = 0; i < size; i++) {
				rth[i].getId();
				rth[i].supportsRuntimeType(null);
			}
		}
	}
}