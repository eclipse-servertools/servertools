/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.util.ProjectModule;

public class PublishOperation2 extends PublishOperation {
	protected TomcatServerBehaviour server;
	protected IModule module;

	public PublishOperation2(TomcatServerBehaviour server, IModule module) {
		super("Publish to server", "Publish Web modules to Tomcat server");
		this.server = server;
		this.module = module;
	}

	public int getOrder() {
		return 0;
	}

	public int getKind() {
		return REQUIRED;
	}

	public void execute(IProgressMonitor monitor, IAdaptable info) throws CoreException {
		//IModuleResourceDelta[] delta = server.getPublishedResourceDelta(new IModule[] { module });
		ProjectModule pm = (ProjectModule) module.loadAdapter(ProjectModule.class, monitor);
		
		IPath path = server.getTempDirectory();
		path = path.append(module.getName());
		copy(pm.members(), path);
	}

	protected void copy(IModuleResource[] resources, IPath path) throws CoreException {
		if (resources == null)
			return;
		
		int size = resources.length;
		for (int i = 0; i < size; i++) {
			copy(resources[i], path);
		}
	}

	protected void copy(IModuleResource resource, IPath path) throws CoreException {
		if (resource instanceof IModuleFolder) {
			IModuleFolder folder = (IModuleFolder) resource;
			copy(folder.members(), path);
		} else {
			IModuleFile mf = (IModuleFile) resource;
			IFile file = (IFile) mf.getAdapter(IFile.class);
			IPath path3 = path.append(mf.getModuleRelativePath()).append(mf.getName());
			File f = path3.toFile().getParentFile();
			if (!f.exists())
				f.mkdirs();
			FileUtil.copyFile(file.getContents(), path3.toOSString());
		}
	}
}