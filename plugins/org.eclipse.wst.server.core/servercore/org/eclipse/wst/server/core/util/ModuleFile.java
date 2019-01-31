/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.util;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.model.IModuleFile;
/**
 * An implementation of {@link IModuleFile} for physical files on disk or in the
 * workspace.
 *
 * @since 1.1
 */
public class ModuleFile implements IModuleFile {
	private IFile file;
	private File file2;
	private String name;
	private IPath path;
	private long stamp = -1;

	/**
	 * Creates a workspace module file with the current modification stamp.
	 * 
	 * @param file a file in the workspace
	 * @param name a name
	 * @param path the path to the file
	 */
	public ModuleFile(IFile file, String name, IPath path) {
		if (name == null)
			throw new IllegalArgumentException();
		this.file = file;
		this.name = name;
		this.path = path;
		if (file != null)
			stamp = file.getModificationStamp() + file.getLocalTimeStamp();
	}

	/**
	 * Creates an external module file with the current modification stamp.
	 * 
	 * @param file
	 * @param name
	 * @param path
	 */
	public ModuleFile(File file, String name, IPath path) {
		if (name == null)
			throw new IllegalArgumentException();
		this.file2 = file;
		this.name = name;
		this.path = path;
		if (file2 != null)
			stamp = file2.lastModified();
	}

	/**
	 * Creates a module file with a specific modification stamp and no
	 * file reference.
	 * 
	 * @param name
	 * @param path
	 * @param stamp
	 */
	public ModuleFile(String name, IPath path, long stamp) {
		if (name == null)
			throw new IllegalArgumentException();
		this.name = name;
		this.path = path;
		this.stamp = stamp;
	}

	/**
	 * Creates a workspace module file with a specific modification stamp.
	 * 
	 * @param file
	 * @param name
	 * @param path
	 * @param stamp
	 * @deprecated use one of the top two constructors instead
	 */
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
		if (obj == this)
			return true;
		
		if (!(obj instanceof IModuleFile))
			return false;
		
		IModuleFile mf = (IModuleFile) obj;
		if (!name.equals(mf.getName()))
			return false;
		if (!path.equals(mf.getModuleRelativePath()))
			return false;
		return true;
	}

	public int hashCode() {
		return name.hashCode() * 37 + path.hashCode();
	}

	public Object getAdapter(Class cl) {
		if (IFile.class.equals(cl))
			return file;
		if (File.class.equals(cl))
			return file2;
		return null;
	}

	public String toString() {
		return "ModuleFile [" + name + ", " + path + ", " + stamp + "]";
	}
}