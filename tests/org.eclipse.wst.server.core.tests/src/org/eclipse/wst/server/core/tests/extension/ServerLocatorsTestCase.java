/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ServerLocatorsTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(ServerLocatorsTestCase.class, "ServerLocatorsTestCase");
	}

	public void testServerLocatorsExtension() throws Exception {
		/*IServerLocator[] serverLocators = ServerCore.getser();
		if (runtimeLocators != null) {
			int size = runtimeLocators.length;
			for (int i = 0; i < size; i++)
				System.out.println(runtimeLocators[i].getId() + " - " + runtimeLocators[i].getName());
		}*/
	}
}