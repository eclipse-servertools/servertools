/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
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
import org.eclipse.core.runtime.Path;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleArtifactDelegate;
/**
 * A Web module resource.
 * 
 * @since 1.0
 */
public class WebResource extends ModuleArtifactDelegate {
	private IPath path;

	/**
	 * Create a new reference to a Web resource (HTML, GIF, etc. on a server).
	 * 
	 * @param module a module
	 * @param path a relative path within the module
	 */
	public WebResource(IModule module, IPath path) {
		super(module);
		this.path = path;
	}

	public WebResource() {
		super();
	}

	/**
	 * Return the relative path to the artifact within the module.
	 * 
	 * @return the relative path
	 */
	public IPath getPath() {
		return path;
	}

	/*
	 * @see ModuleArtifactDelegate#getName()
	 */
	public String getName() {
		return path.toString();
	}

	/*
	 * @see ModuleArtifactDelegate#deserialize(String)
	 */
	public void deserialize(String s) {
		int ind = s.indexOf("//");
		super.deserialize(s.substring(0, ind));
		path = new Path(s.substring(ind+2));
	}

	/*
	 * @see ModuleArtifactDelegate#serialize()
	 */
	public String serialize() {
		StringBuffer sb = new StringBuffer(super.serialize());
		sb.append("//");
		sb.append(path.toPortableString());
		return sb.toString();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "WebResource [module=" + getModule() + ", path=" + path + "]";
	}
}