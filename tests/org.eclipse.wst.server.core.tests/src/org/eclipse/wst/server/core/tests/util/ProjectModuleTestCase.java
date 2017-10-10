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
package org.eclipse.wst.server.core.tests.util;

import junit.framework.TestCase;
import org.eclipse.wst.server.core.tests.impl.TestProjectModule;
import org.eclipse.wst.server.core.util.ProjectModule;

public class ProjectModuleTestCase extends TestCase {
	protected static ProjectModule pm;

	protected ProjectModule getProjectModule() {
		if (pm == null) {
			pm = new TestProjectModule(null);
		}
		return pm;
	}

	public void testCreate() {
		new TestProjectModule();
	}

	public void testGetProject() {
		getProjectModule().getProject();
	}

	public void testGetId() {
		try {
			getProjectModule().getId();
		} catch (Exception e) {
			// ignore
		}
	}

	public void testValidate() {
		getProjectModule().validate();
	}

	public void testMembers() {
		try {
			getProjectModule().members();
		} catch (Exception e) {
			// ignore
		}
	}

	public void testGetName() {
		try {
			getProjectModule().getName();
		} catch (Exception e) {
			// ignore
		}
	}

	public void testExists() {
		getProjectModule().exists();
	}

	public void testEquals() {
		getProjectModule().equals(null);
	}

	public void testChildModules() {
		getProjectModule().getChildModules();
	}
}