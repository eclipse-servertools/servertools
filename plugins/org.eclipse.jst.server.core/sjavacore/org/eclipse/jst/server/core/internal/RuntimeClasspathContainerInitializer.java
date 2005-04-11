/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.*;
import org.eclipse.jst.server.core.*;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeTargetHandler;
import org.eclipse.wst.server.core.ServerCore;
/**
 * 
 */
public class RuntimeClasspathContainerInitializer extends ClasspathContainerInitializer {
	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#initialize(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
	 */
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
		if (containerPath.segmentCount() > 0) {
			if (containerPath.segment(0).equals(RuntimeClasspathContainer.SERVER_CONTAINER)) {
				ClasspathRuntimeTargetHandler crth = null;
				IRuntime runtime = null;
				String id = "";
				if (containerPath.segmentCount() > 2) {
					IRuntimeTargetHandler handler = ServerCore.findRuntimeTargetHandler(containerPath.segment(1));
					if (handler != null)
						crth = (ClasspathRuntimeTargetHandler) handler.getAdapter(ClasspathRuntimeTargetHandler.class);
					String runtimeId = containerPath.segment(2);
					if (runtimeId != null)
						runtime = ServerCore.findRuntime(runtimeId);
					if (containerPath.segmentCount() > 3)
						id = containerPath.segment(3);
				}
				RuntimeClasspathContainer container = new RuntimeClasspathContainer(containerPath, crth, runtime, id);
				JavaCore.setClasspathContainer(containerPath, new IJavaProject[] {project}, new IClasspathContainer[] {container}, null);
			}
		}
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#getDescription(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
	 */
	public String getDescription(IPath containerPath, IJavaProject project) {
		return Messages.classpathContainerDescription;
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#canUpdateClasspathContainer(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
	 */
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
		return true;
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#requestClasspathContainerUpdate(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject, org.eclipse.jdt.core.IClasspathContainer)
	 */
	public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion) throws CoreException {
		if (containerPath.segmentCount() > 0) {
			if (containerPath.segment(0).equals(RuntimeClasspathContainer.SERVER_CONTAINER)) {
				ClasspathRuntimeTargetHandler crth = null;
				IRuntime runtime = null;
				String id = "";
				if (containerPath.segmentCount() > 2) {
					IRuntimeTargetHandler handler = ServerCore.findRuntimeTargetHandler(containerPath.segment(1));
					if (handler != null) {
						crth = (ClasspathRuntimeTargetHandler) handler.getAdapter(ClasspathRuntimeTargetHandler.class);
					}
					String runtimeId = containerPath.segment(2);
					if (runtimeId != null)
						runtime = ServerCore.findRuntime(runtimeId);
					if (containerPath.segmentCount() > 3)
						id = containerPath.segment(3);
					crth.requestClasspathContainerUpdate(runtime, id, containerSuggestion.getClasspathEntries());
				}
			}
		}
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#getComparisonID(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
	 */
	public Object getComparisonID(IPath containerPath, IJavaProject project) {
		if (containerPath == null)
			return null;
		
		return containerPath.toPortableString();
	}
}