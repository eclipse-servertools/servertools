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
package org.eclipse.jst.server.core.tests;

import org.eclipse.jst.server.core.ClasspathRuntimeTargetHandler;
import org.eclipse.jst.server.core.tests.impl.TestClasspathRuntimeTargetHandler;
import junit.framework.Test;
import junit.framework.TestCase;

public class ClasspathRuntimeTargetHandlerTestCase extends TestCase {
	protected static ClasspathRuntimeTargetHandler handler;

	public static Test suite() {
		return new OrderedTestSuite(ClasspathRuntimeTargetHandlerTestCase.class, "ClasspathRuntimeTargetHandlerTestCase");
	}

	public void test00Create() {
		handler = new TestClasspathRuntimeTargetHandler();
	}

	public void test01GetClasspathContainerLabel() {
		handler.getClasspathContainerLabel(null, null);
	}

	public void test02GetClasspathEntryIds() {
		handler.getClasspathEntryIds();
	}

	public void test03ResolveClasspathContainer() {
		handler.resolveClasspathContainer(null, null);
	}
	
	public void test04ResolveClasspathContainerImpl() {
		handler.resolveClasspathContainerImpl(null, null);
	}
	
	public void test05TestAddMethods() {
		((TestClasspathRuntimeTargetHandler) handler).testAddMethods();
	}
	
	public void test06SetRuntimeTarget() throws Exception {
		handler.setRuntimeTarget(null, null, null);
	}
	
	public void test07RemoveRuntimeTarget() {
		handler.removeRuntimeTarget(null, null, null);
	}
	
	public void test09GetDelegateClasspathEntries() {
		handler.getDelegateClasspathEntries(null, null);
	}
	
	public void test10RequestClasspathContainerUpdate() {
		handler.requestClasspathContainerUpdate(null, null, null);
	}
}