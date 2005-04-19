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
package org.eclipse.wst.server.core.tests.impl;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.server.core.util.ProjectModule;

public class TestProjectModule extends ProjectModule {
	public TestProjectModule() {
		super();
	}
	
	public TestProjectModule(IProject project) {
		super(project);
	}
	
	public void testProtected() throws Exception {
		try {
			getModuleResources(null, null);
		} catch (Exception e) {
			// ignore
		}
		try {
			fireModuleChangeEvent(false, null, null, null);
		} catch (Exception e) {
			// ignore
		}
		update();
	}
}