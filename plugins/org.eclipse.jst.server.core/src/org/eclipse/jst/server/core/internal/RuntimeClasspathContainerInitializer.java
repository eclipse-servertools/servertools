/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
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
				RuntimeClasspathProviderWrapper delegate = null;
				IRuntime runtime = null;
				String runtimeId = null;
				if (containerPath.segmentCount() > 2) {
					delegate = JavaServerPlugin.findRuntimeClasspathProvider(containerPath.segment(1));
					
					runtimeId = containerPath.segment(2);
					if (runtimeId != null)
						runtime = ServerCore.findRuntime(runtimeId);
				}
				RuntimeClasspathContainer container = new RuntimeClasspathContainer(project.getProject(), containerPath, delegate, runtime, runtimeId);
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
				RuntimeClasspathProviderWrapper delegate = null;
				IRuntime runtime = null;
				if (containerPath.segmentCount() > 2) {
					delegate = JavaServerPlugin.findRuntimeClasspathProvider(containerPath.segment(1));
					String runtimeId = containerPath.segment(2);
					if (runtimeId != null)
						runtime = ServerCore.findRuntime(runtimeId);
					delegate.requestClasspathContainerUpdate(runtime, containerSuggestion.getClasspathEntries());
					//JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project },
					//		new IClasspathContainer[] { containerSuggestion }, new NullProgressMonitor());
					updateClasspath(runtime, containerPath, containerSuggestion);
				}
			}
		}
	}

	public static void updateClasspath(final IRuntime runtime, final IPath containerPath, final IClasspathContainer containerSuggestion) {
		class UpdateClasspathJob extends Job {
			public UpdateClasspathJob() {
				super(NLS.bind(Messages.updateClasspathContainers, runtime.getName()));
			}

			public boolean belongsTo(Object family) {
				return ServerUtil.SERVER_JOB_FAMILY.equals(family);
			}

			public IStatus run(IProgressMonitor monitor) {
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				List<IJavaProject> list = new ArrayList<IJavaProject>();
				if (projects != null) {
					for (IProject project : projects) {
						if (project.isAccessible()) {
							try {
								if (!project.isNatureEnabled(JavaCore.NATURE_ID))
									continue;
								
								IJavaProject javaProject = JavaCore.create(project);
								
								boolean found = false;
								IClasspathEntry[] ce = javaProject.getRawClasspath();
								for (IClasspathEntry cp : ce) {
									if (cp.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
										if (containerPath.isPrefixOf(cp.getPath()))
											found = true;
									}
								}
								
								if (Trace.FINEST) {
									Trace.trace(Trace.STRING_FINEST, "Classpath change on: " + project + " " + found);
								}
								
								if (found)
									list.add(javaProject);
							} catch (Exception e) {
								if (Trace.SEVERE) {
									Trace.trace(Trace.STRING_SEVERE, "Could not update classpath container", e);
								}
							}
						}
					}
				}
				
				int size = list.size();
				if (size > 0) {
					IJavaProject[] javaProjects = new IJavaProject[size];
					list.toArray(javaProjects);
					IClasspathContainer[] containers = new IClasspathContainer[size];
					for (int i = 0; i < size; i++)
						containers[i] = containerSuggestion;
					
					try {
						JavaCore.setClasspathContainer(containerPath, javaProjects, containers, monitor);
					} catch (JavaModelException jme) {
						return jme.getStatus();
					}
				}
				
				return Status.OK_STATUS;
			}
		}
		UpdateClasspathJob job = new UpdateClasspathJob();
		job.schedule();
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