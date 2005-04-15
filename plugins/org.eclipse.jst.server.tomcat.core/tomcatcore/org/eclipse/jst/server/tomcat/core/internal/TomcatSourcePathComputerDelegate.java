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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.*;
/**
 *
 */
public class TomcatSourcePathComputerDelegate implements ISourcePathComputerDelegate {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedSourceLookupPath(configuration);

		IServer server = ServerUtil.getServer(configuration);
		if (server != null) {
			List list = new ArrayList();
			List pathList = new ArrayList();
			IModule[] modules = server.getModules();
			for (int i = 0; i < modules.length; i++) {
				IProject project = modules[i].getProject();
				if (project != null) {
					try {
						if (project.hasNature(JavaCore.NATURE_ID)) {
							IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
							list.add(javaProject);
						}
					} catch (Exception e) {
						// ignore
					}
					
					IPath path = server.getRuntime().getLocation().append("work").append("Catalina").append("localhost").append(modules[i].getName());
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
		return JavaRuntime.getSourceContainers(resolved);
	}
}