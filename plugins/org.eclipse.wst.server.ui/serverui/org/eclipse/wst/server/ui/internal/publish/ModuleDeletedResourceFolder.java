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
/**
 * Folder of deleted resources for a project.
 */
public class ModuleDeletedResourceFolder {
	protected IModule module;
	
	/**
	 * ProjectDeletedResourceFolder constructor comment.
	 */
	public ModuleDeletedResourceFolder(IModule module) {
		super();
		this.module = module;
	}

	/**
	 * Returns true if the object is equal to this one.
	 *
	 * @param obj java.lang.Object
	 * @return boolean
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ModuleDeletedResourceFolder))
			return false;

		ModuleDeletedResourceFolder folder = (ModuleDeletedResourceFolder) obj;
		return (module != null && module.equals(folder.getModule()));
	}

	/**
	 * Returns the project.
	 *
	 * @return org.eclipse.core.resources.IProject
	 */
	public IModule getModule() {
		return module;
	}
}
