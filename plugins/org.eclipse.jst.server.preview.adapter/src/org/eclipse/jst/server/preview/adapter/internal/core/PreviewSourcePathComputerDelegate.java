/**********************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
		List<IRuntimeClasspathEntry> classpaths = new ArrayList<IRuntimeClasspathEntry>();
		classpaths.addAll(Arrays.asList(JavaRuntime.computeUnresolvedSourceLookupPath(configuration)));
		List<ISourceContainer> sourcefolderList = new ArrayList<ISourceContainer>();
		
		IServer server = ServerUtil.getServer(configuration);
		if (server != null) {
			List<IJavaProject> list = new ArrayList<IJavaProject>();
			IModule[] modules = server.getModules();
			for (IModule module : modules) {
				IProject project = module.getProject();
				if (project != null) {
					IFolder moduleFolder = project.getFolder(module.getName());
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
			
			for (IJavaProject project : projects)
				classpaths.addAll(Arrays.asList(JavaRuntime.computeUnresolvedRuntimeClasspath(project)));
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