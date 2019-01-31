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
package org.eclipse.jst.server.core.tests.impl;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.server.core.IEnterpriseApplication;
import org.eclipse.jst.server.core.IJ2EEModule;
import org.eclipse.wst.server.core.IModule;

public class TestEnterpriseApplication implements IEnterpriseApplication {
	public String getJ2EESpecificationVersion() {
		return null;
	}

	public IPath getLocation() {
		return null;
	}

	public IModule[] getModules() {
		return null;
	}

	public String getURI(IJ2EEModule module) {
		return null;
	}

	public boolean containsLooseModules() {
		return false;
	}

	public String getURI(IModule module) {
		return null;
	}

	public IContainer[] getResourceFolders() {
		return null;
	}
}