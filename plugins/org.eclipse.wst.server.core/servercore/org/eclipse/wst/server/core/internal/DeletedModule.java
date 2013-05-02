/*******************************************************************************
 * Copyright (c) 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
/**
 * 
 */
public class DeletedModule implements IModule {
	protected String id;
	protected String name;
	protected IModuleType moduleType2;
	protected boolean isExternal;

	public DeletedModule(String id, String name, IModuleType moduleType) {
		this(id, name, moduleType, false);
	}

	public DeletedModule(String id, String name, IModuleType moduleType, boolean isExternal) {
		this.id = id;
		this.name = name;
		this.moduleType2 = moduleType;
		this.isExternal = isExternal;
	}

	public String getId() {
		return id;
	}

	public IStatus validate(IProgressMonitor monitor) {
		return null;
	}

	public String getName() {
		return name;
	}

	public IModuleType getModuleType() {
		return moduleType2;
	}

	public IProject getProject() {
		return null;
	}

	public IModule[] getChildModules(IProgressMonitor monitor) {
		return null;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public Object loadAdapter(Class adapter, IProgressMonitor monitor) {
		return null;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof IModule))
			return false;
			
		IModule m = (IModule) obj;
		if (!getId().equals(m.getId()))
			return false;
			
		return true;
	}

	public boolean isExternal() {
		return isExternal;
	}

	public boolean exists() {
		return false;
	}

	public String toString() {
		return "DeletedModule[" + name + "," + id + "]";
	}
}