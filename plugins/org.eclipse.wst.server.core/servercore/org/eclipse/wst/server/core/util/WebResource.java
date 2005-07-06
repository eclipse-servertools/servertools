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
package org.eclipse.wst.server.core.util;

import org.eclipse.core.runtime.IPath;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
/**
 * 
 * @plannedfor 1.0
 */
public class WebResource implements IModuleArtifact {
	private IModule module;
	private IPath path;

	/**
	 * Create a new reference to a Web resource (HTML, GIF, etc. on a server).
	 * 
	 * @param module a module
	 * @param path a relative path within the module
	 */
	public WebResource(IModule module, IPath path) {
		this.module = module;
		this.path = path;
	}

	/**
	 * @see IModuleArtifact#getModule()
	 */
	public IModule getModule() {
		return module;
	}

	/**
	 * Return the relative path to the artifact within the module.
	 * 
	 * @return the relative path
	 */
	public IPath getPath() {
		return path;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "WebResource [module=" + module + ", path=" + path + "]";
	}
}