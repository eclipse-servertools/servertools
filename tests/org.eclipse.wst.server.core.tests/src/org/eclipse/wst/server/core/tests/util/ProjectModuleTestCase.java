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
package org.eclipse.wst.server.core.tests.util;

import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.tests.impl.TestProjectModule;
import org.eclipse.wst.server.core.util.ProjectModule;
import junit.framework.Test;
import junit.framework.TestCase;

public class ProjectModuleTestCase extends TestCase {
	protected static ProjectModule pm;

	public static Test suite() {
		return new OrderedTestSuite(ProjectModuleTestCase.class, "ProjectModuleTestCase");
	}

	public void test00Create() {
		pm = new TestProjectModule();
	}

	public void test01Create() {
		pm = new TestProjectModule(null);
	}
	
	public void test02GetProject() {
		pm.getProject();
	}
	
	public void test03GetRootFolder() {
		pm.getRootFolder();
	}
	
	public void test04GetId() {
		try {
			pm.getId();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test05Validate() {
		pm.validate();
	}
	
	public void test06Members() {
		try {
			pm.members();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test07GetName() {
		try {
			pm.getName();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test08Exists() {
		pm.exists();
	}
	
	public void test09Equals() {
		pm.equals(null);
	}

	public void test10() {
		pm.getChildModules();
	}
	
	public void test11TestProtected() throws Exception {
		((TestProjectModule)pm).testProtected();
	}
}