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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.model.IModuleFile;
/**
 * 
 */
public class ModuleFile implements IModuleFile {
	protected IFile file;
	protected String name;
	protected IPath path;
	protected long stamp;
	
	public ModuleFile(IFile file, String name, IPath path, long stamp) {
		this.file = file;
		this.name = name;
		this.path = path;
		this.stamp = stamp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleFile#getModificationStamp()
	 */
	public long getModificationStamp() {
		return stamp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleResource#getModuleRelativePath()
	 */
	public IPath getModuleRelativePath() {
		return path;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleResource#getName()
	 */
	public String getName() {
		return name;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ModuleFile))
			return false;
		
		ModuleFile mf = (ModuleFile) obj;
		if (!name.equals(mf.name))
			return false;
		if (!path.equals(mf.path))
			return false;
		return true;
	}

	public int hashCode() {
		return name.hashCode() * 37 + path.hashCode();
	}

	public Object getAdapter(Class cl) {
		if (IFile.class.equals(cl))
			return file;
		return null;
	}

	public String toString() {
		return "ModuleFile [" + name + ", " + path + ", " + stamp + "]";
	}
}