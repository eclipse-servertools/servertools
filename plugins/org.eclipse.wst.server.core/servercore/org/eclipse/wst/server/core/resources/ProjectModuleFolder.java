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
package org.eclipse.wst.server.core.resources;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.model.IModule;
/**
 * 
 */
public class ProjectModuleFolder extends ProjectModuleResource implements IModuleFolder {
	public ProjectModuleFolder(IModule module, IModuleFolder parent, IContainer container) {
		super(module, parent, container);
	}

	/**
	 * Returns a list of IModuleResources directly contained
	 * within this folder.
	 *
	 * @return java.util.List
	 */
	public IModuleResource[] members() throws CoreException {
		IContainer container = (IContainer) getResource();
		IResource[] resources = container.members();
		if (resources != null) {
			List list = new ArrayList();
			
			int size = resources.length;
			for (int i = 0; i < size; i++) {
				IResource res = resources[i];
				if (res instanceof IContainer) {
					ProjectModuleFolder pdf = new ProjectModuleFolder(getModule(), this, (IContainer) res);	
					list.add(pdf);
				} else if (res instanceof IFile) {
					ProjectModuleFile pdf = new ProjectModuleFile(getModule(), this, (IFile) res);
					list.add(pdf);
				}
			}
			
			IModuleResource[] moduleResources = new IModuleResource[list.size()];
			list.toArray(moduleResources);
			return moduleResources;
		}
		return null;
	}
}