/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleListener;
/**
 * A simple IModule that represents a missing or unavailable
 * module.
 */
public class MissingModule implements IModule {
	protected String name;
	protected String id;

	public MissingModule(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/*
	 * @see IModuleProject#getModuleResourceDelta(IResourceDelta)
	 */
	public IPath getModuleResourceDelta(IResourceDelta delta) {
		return null;
	}

	/*
	 * @see IModule#getFactoryId()
	 */
	public String getFactoryId() {
		return "org.eclipse.wst.server.core.missingModuleFactory";
	}

	/*
	 * @see IModule#getMemento()
	 */
	public String getMemento() {
		return id + "/" + name;
	}
	
	public String getType() {
		return "";
	}
	
	public String getVersion() {
		return "";
	}
	
	/*
	 * @see IModule#getPublishStatus()
	 */
	public IStatus validate() {
		return null;
	}

	/*
	 * @see IModule#getPublishStatus()
	 */
	public IStatus canPublish() {
		return null;
	}

	/*
	 * @see IModule#members()
	 */
	/*public IModuleResource[] members() {
		return new IModuleResource[0];
	}*/

	/*
	 * @see IModule#getName()
	 */
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	/**
	 * Returns true if this module currently exists, and false if it has
	 * been deleted or moved and is no longer represented by this module.
	 *
	 * @return boolean
	 */
	public boolean exists() {
		return false;
	}
	
	/**
	 * 
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof MissingModule))
			return false;

		MissingModule md = (MissingModule) obj;
		return (md.getId().equals(id));
	}
	
	/**
	 * Add a listener for the module.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleListener
	 */
	public void addModuleListener(IModuleListener listener) {
	}
	
	/**
	 * Remove a listener from the module.
	 * 
	 * @param listener org.eclipse.wst.server.core.model.IModuleListener
	 */
	public void removeModuleListener(IModuleListener listener) {
	}
	
	public IModule[] getChildModules() {
		return null;
	}
}