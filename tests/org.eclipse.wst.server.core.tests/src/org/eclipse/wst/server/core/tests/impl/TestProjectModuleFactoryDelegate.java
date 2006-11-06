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
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;

public class TestProjectModuleFactoryDelegate extends ProjectModuleFactoryDelegate {
	public TestProjectModuleFactoryDelegate() {
		super();
	}

	public void testProtected() throws Exception {
		createModules(null);
		getListenerPaths();
		isValidModule(null);
		try {
			createModule(null, null, null, null, null);
		} catch (Exception e) {
			// ignore
		}
		try {
			handleGlobalProjectChange(null, null);
		} catch (Exception e) {
			// ignore
		}
	}

	protected boolean isValidModule(IProject project) {
		return false;
	}

	protected IModule[] createModules(IProject project) {
		return null;
	}

	public ModuleDelegate getModuleDelegate(IModule module) {
		return null;
	}

	protected IModule createModule(IProject project) {
		return null;
	}
}