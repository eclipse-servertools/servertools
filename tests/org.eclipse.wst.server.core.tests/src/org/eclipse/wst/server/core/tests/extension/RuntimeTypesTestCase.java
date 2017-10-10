/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;

public class RuntimeTypesTestCase extends TestCase {
	public void testRuntimeTypesExtension() throws Exception {
		IRuntimeType[] runtimeTypes = ServerCore.getRuntimeTypes();
		if (runtimeTypes != null) {
			for (IRuntimeType runtimeType : runtimeTypes) {
				runtimeType.getId();
				runtimeType.getName();
				runtimeType.canCreate();
				runtimeType.getDescription();
				runtimeType.getVendor();
				runtimeType.getVersion();
				runtimeType.getModuleTypes();
			}
		}
	}
}