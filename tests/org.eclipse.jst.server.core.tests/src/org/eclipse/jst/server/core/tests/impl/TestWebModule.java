/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
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

import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.wst.server.core.IModule;

public class TestWebModule extends TestJ2EEModule implements IWebModule {
	public String getServletSpecificationVersion() {
		return null;
	}

	public String getJSPSpecificationVersion() {
		return null;
	}

	public String getContextRoot() {
		return null;
	}

	public boolean isPublishRequired() {
		return false;
	}

	public IModule[] getModules() {
		return null;
	}

	public String getURI(IModule module) {
		return null;
	}

	public String getContextRoot(IModule earModule) {
		return null;
	}
}