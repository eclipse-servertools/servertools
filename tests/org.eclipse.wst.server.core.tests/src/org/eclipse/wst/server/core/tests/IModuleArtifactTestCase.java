/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;

import junit.framework.TestCase;

public class IModuleArtifactTestCase extends TestCase {
	protected static IModuleArtifact artifact;

	public void testCreate() {
		artifact = new IModuleArtifact() {
			public IModule getModule() {
				return null;
			}
		};
		
		artifact.getModule();
	}
}