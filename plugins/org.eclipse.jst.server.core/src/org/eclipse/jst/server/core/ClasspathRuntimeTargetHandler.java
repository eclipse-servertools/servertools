/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.model.RuntimeTargetHandlerDelegate;
/**
 * A runtime target handler that supports changing the classpath of the
 * project by adding one or more classpath containers. Runtime providers
 * can extend this class and implement the abstract methods to provide
 * the correct build path for their runtime type.
 * 
 * @deprecated Should use org.eclipse.jst.server.core.runtimeClasspathProviders
 *    extension point instead
 */
public abstract class ClasspathRuntimeTargetHandler extends RuntimeTargetHandlerDelegate {
	/** (non-Javadoc)
	 * @see RuntimeTargetHandlerDelegate#setRuntimeTarget(IProject, IRuntime, IProgressMonitor)
	 */
	public void setRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Attempt to use deprecated code");
	}

	/** (non-Javadoc)
	 * @see RuntimeTargetHandlerDelegate#removeRuntimeTarget(IProject, IRuntime, IProgressMonitor)
	 */
	public void removeRuntimeTarget(IProject project, IRuntime runtime, IProgressMonitor monitor) {
		throw new RuntimeException("Attempt to use deprecated code");
	}

	/**
	 * Add library entries to the given list for every jar file found in the
	 * given directory. Optionally search subdirectories as well.
	 * 
	 * @param list a list
	 * @param dir a directory
	 * @param includeSubdirectories <code>true</code> to include subdirectories, and
	 *    <code>false</code> otherwise
	 */
	protected static void addLibraryEntries(List list, File dir, boolean includeSubdirectories) {
		throw new RuntimeException("Attempt to use deprecated code");
	}

	/**
	 * Returns the classpath entries that correspond to the given runtime.
	 * 
	 * @param runtime a runtime
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return an array of classpath entries
	 */
	public IClasspathEntry[] getDelegateClasspathEntries(IRuntime runtime, IProgressMonitor monitor) {
		throw new RuntimeException("Attempt to use deprecated code");
	}

	/**
	 * Returns the classpath entry ids for this runtime target handler. These
	 * ids will be added to the classpath container id to create a new fully
	 * qualified classpath container id.
	 * <p>
	 * By default, there is a single classpath entry for the runtime, with no
	 * extra id (<code>new String[1]</code>). To create multiple ids, just
	 * return a string array containing the ids. For instance, to have two
	 * classpath containers with ids "id1" and "id2", use
	 * <code>new String[] { "id1", "id2" }</code>
	 * </p>
	 * 
	 * @return an array of classpath entry ids
	 */
	public String[] getClasspathEntryIds() {
		throw new RuntimeException("Attempt to use deprecated code");
	}

	/**
	 * Request that the classpath container for the given runtime and id be updated
	 * with the given classpath container entries.
	 * 
	 * @param runtime a runtime
	 * @param id an id
	 * @param entries an array of classpath entries
	 */
	public void requestClasspathContainerUpdate(IRuntime runtime, String id, IClasspathEntry[] entries) {
		throw new RuntimeException("Attempt to use deprecated code");
	}

	/**
	 * Returns the classpath container label for the given runtime and the given
	 * classpath container id (returned from getClasspathEntryIds()). This method
	 * must not return null.
	 * 
	 * @param runtime the runtime to resolve the container label for
	 * @param id the classpath entry id
	 * @return a classpath container label
	 */
	public abstract String getClasspathContainerLabel(IRuntime runtime, String id);

	/**
	 * Resolve the classpath container.
	 * 
	 * @param runtime a runtime
	 * @param id a container id
	 * @return a possibly empty array of classpath entries
	 */
	public IClasspathEntry[] resolveClasspathContainerImpl(IRuntime runtime, String id) {
		throw new RuntimeException("Attempt to use deprecated code");
	}

	/**
	 * Resolves (creates the classpath entries for) the classpath container with
	 * the given runtime and the given classpath container id (returned from
	 * getClasspathEntryIds()). If the classpath container cannot be resolved
	 * (for instance, if the runtime does not exist), return null.
	 * 
	 * @param runtime the runtime to resolve the container for
	 * @param id the classpath entry id
	 * @return an array of classpath entries for the container, or null if the
	 *   container could not be resolved
	 */
	public abstract IClasspathEntry[] resolveClasspathContainer(IRuntime runtime, String id);
}
