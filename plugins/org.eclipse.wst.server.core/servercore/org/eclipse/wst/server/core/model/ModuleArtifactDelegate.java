/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * A module artifact is a resource within a module, which can be launched
 * on the server. Examples of module artifacts are servlets, HTML pages,
 * or EJB beans.
 * <p>
 * Objects that provide an adapter to this type will be considered by the
 * contextual Run on Server launch support.  
 * </p>
 * <p>
 * Subclasses should provide a default (no-arg) constructor, implement
 * the serialize/deserialize methods, and provide a useful name.
 * </p>
 * 
 * @see org.eclipse.wst.server.core.model.ModuleArtifactAdapterDelegate
 * @since 2.0
 */
public abstract class ModuleArtifactDelegate implements IModuleArtifact {
	private IModule module;

	/**
	 * Create a new module artifact.
	 * 
	 * @param module a module
	 */
	public ModuleArtifactDelegate(IModule module) {
		this.module = module;
	}

	/**
	 * Create a new module artifact.
	 */
	public ModuleArtifactDelegate() {
		super();
	}

	/**
	 * @see IModuleArtifact#getModule()
	 */
	public IModule getModule() {
		return module;
	}

	/**
	 * Returns a user-presentable name for this artifact.
	 * 
	 * @return a user-presentable name
	 */
	public abstract String getName();

	/**
	 * Serialize this object into a string.
	 *  
	 * @return a serialized string
	 */
	public String serialize() {
		return module.getId();
	}

	/**
	 * Deserialize this object from a serialized string.
	 * 
	 * @param s a serialized string.
	 */
	public void deserialize(String s) {
		this.module = ServerUtil.getModule(s);
	}
}