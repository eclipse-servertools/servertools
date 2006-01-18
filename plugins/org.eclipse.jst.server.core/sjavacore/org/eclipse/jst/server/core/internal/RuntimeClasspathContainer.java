/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;

import org.eclipse.wst.server.core.IRuntime;
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

	/**
	 * Create a new runtime classpath container.
	 * 
	 * @param path
	 * @param delegate
	 * @param runtime
	 * @param id
	 * @deprecated should use the equivalent method without the unused id variable
	 */
	public RuntimeClasspathContainer(IPath path, RuntimeClasspathProviderWrapper delegate, IRuntime runtime, String id) {
		this.path = path;
		this.delegate = delegate;
		this.runtime = runtime;
	}

	/**
	 * Create a new runtime classpath container.
	 * 
	 * @param path
	 * @param delegate
	 * @param runtime
	 */
	public RuntimeClasspathContainer(IPath path, RuntimeClasspathProviderWrapper delegate, IRuntime runtime) {
		this.path = path;
		this.delegate = delegate;
		this.runtime = runtime;
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getClasspathEntries()
	 */
	public IClasspathEntry[] getClasspathEntries() {
		IClasspathEntry[] entries = null;
		if (delegate != null && runtime != null)
			entries = delegate.resolveClasspathContainerImpl(runtime);
		
		if (entries == null)
			return new IClasspathEntry[0];
		
		return entries;
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getDescription()
	 */
	public String getDescription() {
		if (runtime != null && delegate != null) {
			String s = delegate.getClasspathContainerLabel(runtime);
			if (s != null)
				return s;
		}
		
		return Messages.classpathContainerDescription;
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
}