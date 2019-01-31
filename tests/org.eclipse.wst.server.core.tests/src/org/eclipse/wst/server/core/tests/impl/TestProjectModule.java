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
package org.eclipse.wst.server.core.tests.impl;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ProjectModule;

public class TestProjectModule extends ProjectModule {
	public TestProjectModule() {
		super();
	}
	
	public TestProjectModule(IProject project) {
		super(project);
	}

	public IModuleResource[] members() throws CoreException {
		return null;
	}
}