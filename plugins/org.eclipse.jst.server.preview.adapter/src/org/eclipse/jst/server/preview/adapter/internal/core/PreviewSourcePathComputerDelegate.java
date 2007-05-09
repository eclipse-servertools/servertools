/**********************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.preview.adapter.internal.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
/**
 *
 */
public class PreviewSourcePathComputerDelegate implements ISourcePathComputerDelegate {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		List classpaths = new ArrayList();
		classpaths.addAll(Arrays.asList(JavaRuntime.computeUnresolvedSourceLookupPath(configuration)));
		List sourcefolderList = new ArrayList();
		
		IServer server = ServerUtil.getServer(configuration);
		if (server != null) {
			List list = new ArrayList();
			IModule[] modules = server.getModules();
			for (int i = 0; i < modules.length; i++) {
				IProject project = modules[i].getProject();
				if (project != null) {
					IFolder moduleFolder = project.getFolder(modules[i].getName());
					if (moduleFolder.exists()) {
						sourcefolderList.add(new FolderSourceContainer(moduleFolder, true));
					}
					
					try {
						if (project.hasNature(JavaCore.NATURE_ID)) {
							IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
							if (!list.contains(javaProject))
								list.add(javaProject);
						}
					} catch (Exception e) {
						// ignore
					}
				}
			}
			int size = list.size();
			IJavaProject[] projects = new IJavaProject[size];
			list.toArray(projects);
			
			for (int i = 0; i < size; i++)
				classpaths.addAll(Arrays.asList(JavaRuntime.computeUnresolvedRuntimeClasspath(projects[i])));
		}

		IRuntimeClasspathEntry[] entries = new IRuntimeClasspathEntry[classpaths.size()];
		classpaths.toArray(entries);

		IRuntimeClasspathEntry[] resolved = JavaRuntime.resolveSourceLookupPath(entries, configuration);
		ISourceContainer[] sourceContainers = JavaRuntime.getSourceContainers(resolved);
		
		if (!sourcefolderList.isEmpty()) {
			ISourceContainer[] combinedSourceContainers = new ISourceContainer[sourceContainers.length + sourcefolderList.size()];
			sourcefolderList.toArray(combinedSourceContainers);
			System.arraycopy(sourceContainers, 0, combinedSourceContainers, sourcefolderList.size(), sourceContainers.length);
			sourceContainers = combinedSourceContainers;
		}

		return sourceContainers;
	}
}