/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests;

import org.eclipse.wst.server.core.IModuleType;

import junit.framework.TestCase;

public class IModuleTypeTestCase extends TestCase {
	protected static IModuleType type;

	public void testCreate() {
		type = new IModuleType() {
			public String getId() {
				return null;
			}

			public String getName() {
				return null;
			}

			public String getVersion() {
				return null;
			}
		};
		
		type.getId();
		type.getName();
		type.getVersion();
	}
}