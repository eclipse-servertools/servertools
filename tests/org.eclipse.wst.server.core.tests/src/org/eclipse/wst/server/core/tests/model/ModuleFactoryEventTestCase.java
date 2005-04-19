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

import org.eclipse.wst.server.core.model.ModuleFactoryEvent;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;

public class ModuleFactoryEventTestCase extends TestCase {
	protected static ModuleFactoryEvent event;

	public static Test suite() {
		return new OrderedTestSuite(ModuleFactoryEventTestCase.class, "ModuleFactoryEventTestCase");
	}

	public void test00CreateEvent() throws Exception {
		event = new ModuleFactoryEvent(null, null);
	}

	public void test01GetAddedModules() throws Exception {
		event.getAddedModules();
	}

	public void test02GetRemovedModules() throws Exception {
		event.getRemovedModules();
	}
}