/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
public class TomcatSourcePathComputerDelegate implements ISourcePathComputerDelegate {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedSourceLookupPath(configuration);
		List sourcefolderList = new ArrayList();

		IServer server = ServerUtil.getServer(configuration);
		if (server != null) {
			IPath basePath = ((TomcatServerBehaviour)server.getAdapter(TomcatServerBehaviour.class)).getRuntimeBaseDirectory();
			List list = new ArrayList();
			List pathList = new ArrayList();
			IModule[] modules = server.getModules();
			for (int i = 0; i < modules.length; i++) {
				IProject project = modules[i].getProject();
				if (project != null) {
					/**
					 * WORKAROUND for bug 93174,
					 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=93174
					 * 
					 * Assume that a folder with the same name as the IModule
					 * name located directly under the module's project is a
					 * "flexible" module. Alter the behavior so that a folder
					 * container is added instead.
					 */
					IFolder moduleFolder = project.getFolder(modules[i].getName());
					if (moduleFolder.exists()) {
						sourcefolderList.add(new FolderSourceContainer(moduleFolder, true));
					} else {
						try {
							if (project.hasNature(JavaCore.NATURE_ID)) {
								IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
								list.add(javaProject);
							}
						} catch (Exception e) {
							// ignore
						}
					}
					
					IPath path = basePath.append("work").append("Catalina").append("localhost").append(modules[i].getName());
					pathList.add(path);
				}
			}
			int size = list.size();
			IJavaProject[] projects = new IJavaProject[size];
			list.toArray(projects);
			
			int size2 = entries.length;
			int size3 = pathList.size();
			IRuntimeClasspathEntry[] entries2 = new IRuntimeClasspathEntry[size + size2 + size3];
			System.arraycopy(entries, 0, entries2, 0, size2);
			
			for (int i = 0; i < size; i++) {
				entries2[size2 + i] = JavaRuntime.newProjectRuntimeClasspathEntry(projects[i]); 
			}
			
			for (int i = 0; i < size3; i++) {
				entries2[size + size2 + i] = JavaRuntime.newArchiveRuntimeClasspathEntry((IPath) pathList.get(i)); 
			}
			
			entries = entries2;
		}
		
		IRuntimeClasspathEntry[] resolved = JavaRuntime.resolveSourceLookupPath(entries, configuration);
		ISourceContainer[] sourceContainers = JavaRuntime.getSourceContainers(resolved);

		/**
		 * WORKAROUND for bug 93174,
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=93174
		 * 
		 * FolderSourceContainers have a source container of null, so the
		 * mapping of the regular classpath to source containers is done
		 * before adding the known FolderSourceContainers.
		 */
		if (!sourcefolderList.isEmpty()) {
			ISourceContainer[] combinedSourceContainers = new ISourceContainer[sourceContainers.length + sourcefolderList.size()];
			sourcefolderList.toArray(combinedSourceContainers);
			System.arraycopy(sourceContainers, 0, combinedSourceContainers, sourcefolderList.size(), sourceContainers.length);
			sourceContainers = combinedSourceContainers;
		}

		return sourceContainers;
	}
}