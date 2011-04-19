/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.osgi.util.NLS;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
/**
 * 
 */
public class RuntimeClasspathContainer implements IClasspathContainer {
	/**
	 * The server container id.
	 */
	public static final String SERVER_CONTAINER = JavaServerPlugin.PLUGIN_ID + ".container";

	private IPath path;
	private RuntimeClasspathProviderWrapper delegate;
	private IRuntime runtime;
	private String runtimeId;
	private IProject project;

	/**
	 * Create a new runtime classpath container.
	 * 
	 * @param path
	 * @param delegate
	 * @param runtime
	 * @deprecated should use the constructor that accepts a project
	 */
	public RuntimeClasspathContainer(IPath path, RuntimeClasspathProviderWrapper delegate, IRuntime runtime) {
		this.path = path;
		this.delegate = delegate;
		this.runtime = runtime;
	}

	/**
	 * Create a new runtime classpath container.
	 * 
	 * @param project
	 * @param path
	 * @param delegate
	 * @param runtime
	 * @param runtimeId
	 */
	public RuntimeClasspathContainer(IProject project, IPath path, RuntimeClasspathProviderWrapper delegate, IRuntime runtime, String runtimeId) {
		this.project = project;
		this.path = path;
		this.delegate = delegate;
		this.runtime = runtime;
		this.runtimeId = runtimeId;
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getClasspathEntries()
	 */
	public IClasspathEntry[] getClasspathEntries() {
		IClasspathEntry[] entries = null;
		
		IRuntime curRuntime = getRuntime();
		
		if (delegate != null && curRuntime != null)
			entries = delegate.resolveClasspathContainerImpl(project, curRuntime);
		
		if (entries == null)
			return new IClasspathEntry[0];
		
		return entries;
	}

	private IRuntime getRuntime(){
		if (runtime == null && runtimeId != null) {
			// Make sure the runtime object is initialized.
			runtime = ServerCore.findRuntime(runtimeId);
		}
		return runtime;
	}
	
	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getDescription()
	 */
	public String getDescription() {
		IRuntime curRuntime = getRuntime();

		if (curRuntime != null) {
			IRuntimeType runtimeType = curRuntime.getRuntimeType();
			if (runtimeType != null)
				return NLS.bind(Messages.classpathContainer, runtimeType.getName(), curRuntime.getName());
		}
		return NLS.bind(Messages.classpathContainerUnbound, Messages.classpathContainerDescription, runtimeId);
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getKind()
	 */
	public int getKind() {
		return IClasspathContainer.K_APPLICATION;
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getPath()
	 */
	public IPath getPath() {
		return path;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof RuntimeClasspathContainer))
			return false;
		
		RuntimeClasspathContainer rcc = (RuntimeClasspathContainer) obj;
		if (delegate != null && !delegate.equals(rcc.delegate))
			return false;
		
		if (runtime == null && rcc.runtime != null)
			return false;
		
		if (runtime != null && !runtime.equals(rcc.runtime))
			return false;
		
		if (runtimeId != null && !runtimeId.equals(rcc.runtimeId))
			return false;
		
		if (project == null && rcc.project != null)
			return false;
		
		if (project != null && !project.equals(rcc.project))
			return false;
		
		if (path != null && !path.equals(rcc.path))
			return false;
		
		return true;
	}
}
