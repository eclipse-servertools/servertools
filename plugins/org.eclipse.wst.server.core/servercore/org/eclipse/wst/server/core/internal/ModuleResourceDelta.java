/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
/**
 * 
 */
public class ModuleResourceDelta implements IModuleResourceDelta {
	protected IModuleResource resource;
	protected int kind;
	
	protected IModuleResourceDelta[] children;

	public ModuleResourceDelta(IModuleResource resource, int kind) {
		this.resource = resource;
		this.kind = kind;
	}
	
	public void setChildren(IModuleResourceDelta[] children) {
		this.children = children;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleResource#getModuleRelativePath()
	 */
	public IPath getModuleRelativePath() {
		return resource.getModuleRelativePath();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleResourceDelta#getModuleResource()
	 */
	public IModuleResource getModuleResource() {
		return resource;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleResourceDelta#getKind()
	 */
	public int getKind() {
		return kind;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleResourceDelta#getAffectedChildren()
	 */
	public IModuleResourceDelta[] getAffectedChildren() {
		return children;
	}

	public String toString() {
		return "ModuleResourceDelta [" + resource + ", " + kind + "]";
	}
	
	public void trace(String indent) {
		System.out.println(indent + toString());
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				((ModuleResourceDelta)children[i]).trace(indent + "  ");
			}
		}
	}
}