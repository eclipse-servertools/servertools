/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.tests.impl.TestModuleResourceDelta;

public class ModuleResourceDeltaTestCase extends TestCase {
	protected static IModuleResourceDelta delta;

	protected IModuleResourceDelta getModuleResourceDelta() {
		if (delta == null) {
			delta = new TestModuleResourceDelta();
		}
		return delta;
	}

	public void testGetModuleResource() {
		assertNull(getModuleResourceDelta().getModuleResource());
	}
	
	public void testGetAffectedChildren() {
		assertNull(getModuleResourceDelta().getAffectedChildren());
	}
	
	public void testGetModuleRelativePath() {
		assertNull(getModuleResourceDelta().getModuleRelativePath());
	}
	
	public void testGetKind() {
		getModuleResourceDelta().getKind();
	}
}