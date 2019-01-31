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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.ModuleDelegate;

public class TestModuleDelegate extends ModuleDelegate{
	public IStatus validate() {
		return null;
	}

	public IModule[] getChildModules() {
		return null;
	}

	public IModuleResource[] members() throws CoreException {
		return null;
	}
}