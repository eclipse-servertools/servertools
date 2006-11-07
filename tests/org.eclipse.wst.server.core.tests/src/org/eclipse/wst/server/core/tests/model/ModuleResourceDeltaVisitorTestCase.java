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
package org.eclipse.wst.server.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.IModuleResourceDeltaVisitor;
import org.eclipse.wst.server.core.tests.impl.TestModuleResourceDeltaVisitor;

public class ModuleResourceDeltaVisitorTestCase extends TestCase {
	protected static IModuleResourceDeltaVisitor delta;

	public void test00CreateDelegate() {
		delta = new TestModuleResourceDeltaVisitor();
	}
	
	public void test01Visit() throws Exception {
		delta.visit(null);
	}
}