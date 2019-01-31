/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
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
package org.eclipse.wst.server.core.internal;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
/**
 * @deprecated use org.eclipse.wst.server.core.util.ModuleFile instead
 */
public class ModuleFile extends org.eclipse.wst.server.core.util.ModuleFile {
	public ModuleFile(IFile file, String name, IPath path) {
		super(file, name, path);
	}

	public ModuleFile(String name, IPath path, long stamp) {
		super(name, path, stamp);
	}

	public ModuleFile(File file, String name, IPath path) {
		super(file, name, path);
	}

	/*
	 * @deprecated use another constructor
	 */
	public ModuleFile(IFile file, String name, IPath path, long stamp) {
		super(file, name, path, stamp);
	}
}