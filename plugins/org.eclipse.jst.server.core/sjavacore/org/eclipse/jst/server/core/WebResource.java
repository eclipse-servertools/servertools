/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import org.eclipse.core.runtime.IPath;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
/**
 * 
 * @since 1.0
 */
public class WebResource implements IModuleArtifact {
	private IModule module;
	private IPath path;

	public WebResource(IModule module, IPath path) {
		this.module = module;
		this.path = path;
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