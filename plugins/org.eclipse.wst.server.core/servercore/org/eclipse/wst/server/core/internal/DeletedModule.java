/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
/**
 * 
 * @since 1.0
 */
public class DeletedModule implements IModule {
	protected String id;
	
	public DeletedModule(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public IStatus validate(IProgressMonitor monitor) {
		return null;
	}

	public String getName() {
		return null;
	}

	public IModuleType getModuleType() {
		return null;
	}

	public IProject getProject() {
		return null;
	}

	public void addModuleListener(IModuleListener listener) {
		// ignore
	}

	public void removeModuleListener(IModuleListener listener) {
		// ignore
	}

	public IModule[] getChildModules(IProgressMonitor monitor) {
		return null;
	}

	public Object getAdapter(Class adapter) {
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
	
	public String toString() {
		return "Deleted module (" + getId() + ")";
	}
}