/*******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel R. Somerfield - initial API and implementation
 *******************************************************************************/

package org.eclipse.jst.server.core.internal.cactus;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;

public class WebTestableResource implements IModuleArtifact {
	private IModule fModule;
	private boolean fServletIsConfigured;
	private String fClassName;
	private String fTestName;
	private String fProjectName;

	public WebTestableResource(IModule module, boolean servletIsConfigured,
			String projectName, String className, String testName) {
		fModule = module;
		fServletIsConfigured = servletIsConfigured;
		fClassName = className;
		fTestName = testName;
		fProjectName = projectName;
	}

	public String getProjectName() {
		return fProjectName;
	}

	public boolean isServletConfigured() {
		return fServletIsConfigured;
	}

	public IModule getModule() {
		return fModule;
	}

	public String getClassName() {
		return fClassName;
	}

	public String getTestName() {
		return fTestName;
	}
}