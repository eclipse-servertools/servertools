/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.j2ee;

import org.eclipse.core.runtime.IPath;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
/**
 * 
 */
public class WebResource implements IModuleArtifact {
	public static final String ID = "org.eclipse.jst.server.j2ee.webresource";

	private IModule module;
	private IPath path;

	public WebResource(IModule module, IPath path) {
		this.module = module;
		this.path = path;
	}

	public String getId() {
		return ID;
	}

	public IModule getModule() {
		return module;
	}

	public IPath getPath() {
		return path;
	}

	public String toString() {
		return "WebResource [module=" + module + ", path=" + path + "]";
	}
}