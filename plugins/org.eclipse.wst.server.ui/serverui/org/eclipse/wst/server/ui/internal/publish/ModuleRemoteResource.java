package org.eclipse.wst.server.ui.internal.publish;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.resources.IRemoteResource;
/**
 * Helper class to keep a reference to the project that a
 * remote resource belongs to.
 */
public class ModuleRemoteResource {
	protected IRemoteResource remote;
	protected ModuleDeletedResourceFolder folder;
	protected IModule module;

	/**
	 * ProjectRemoteResource constructor comment.
	 */
	public ModuleRemoteResource(ModuleDeletedResourceFolder folder, IModule module, IRemoteResource remote) {
		super();
		this.folder = folder;
		this.module = module;
		this.remote = remote;
	}

	/**
	 * Returns true if the object is equal to this one.
	 *
	 * @param obj java.lang.Object
	 * @return boolean
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ModuleRemoteResource))
			return false;

		ModuleRemoteResource remote2 = (ModuleRemoteResource) obj;
		return (module != null && module.equals(remote2.getModule()) &&
			remote != null && remote.equals(remote2.getRemote()));
	}

	/**
	 * Returns the folder.
	 *
	 * @return 
	 */
	public ModuleDeletedResourceFolder getFolder() {
		return folder;
	}

	/**
	 * Returns the project.
	 *
	 * @return org.eclipse.core.resources.IProject
	 */
	public IModule getModule() {
		return module;
	}

	/**
	 * Returns the remote resource.
	 *
	 * @return org.eclipse.wst.server.core.model.IRemoteResource
	 */
	public IRemoteResource getRemote() {
		return remote;
	}

	/**
	 * Converts to a String.
	 *
	 * @return java.lang.String
	 */
	public String toString() {
		return remote.toString();
	}
}
