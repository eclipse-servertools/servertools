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

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.ModuleEvent;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;

public class ModuleEventTestCase extends TestCase {
	protected static ModuleEvent event;

	public static Test suite() {
		return new OrderedTestSuite(ModuleEventTestCase.class, "ModuleEventTestCase");
	}

	public void test00CreateEvent() throws Exception {
		event = new ModuleEvent(null, false, null, null, null);
	}
	
	public void test01GetAddedChildModules() throws Exception {
		event.getAddedChildModules();
	}
	
	public void test02GetChangedChildModules() throws Exception {
		event.getChangedChildModules();
	}
	
	public void test03GetRemovedChildModules() throws Exception {
		event.getRemovedChildModules();
	}
	
	public void test04GetChangedArtifacts() throws Exception {
		event.getChangedArtifacts();
	}
	
	public void test05IsChanged() throws Exception {
		event.isChanged();
	}
	
	public void test06GetModule() throws Exception {
		event.getModule();
	}
}