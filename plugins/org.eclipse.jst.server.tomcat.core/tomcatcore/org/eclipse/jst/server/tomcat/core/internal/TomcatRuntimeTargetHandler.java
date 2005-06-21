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
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.ClasspathRuntimeTargetHandler;

import org.eclipse.wst.server.core.IRuntime;
/**
 * 
 */
public class TomcatRuntimeTargetHandler extends ClasspathRuntimeTargetHandler {
	public IClasspathEntry[] getDelegateClasspathEntries(IRuntime runtime, IProgressMonitor monitor) {
		ITomcatRuntime tomcatRuntime = (ITomcatRuntime) runtime.loadAdapter(ITomcatRuntime.class, null);
		IVMInstall vmInstall = tomcatRuntime.getVMInstall();
		if (vmInstall != null) {
			String name = vmInstall.getName();
			String typeId = vmInstall.getVMInstallType().getId();
			return new IClasspathEntry[] { JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER).append(typeId).append(name)) };
		}
		return null;
	}

	/**
	 * @see ClasspathRuntimeTargetHandler#getClasspathContainerLabel(IRuntime, String)
	 */
	public String getClasspathContainerLabel(IRuntime runtime, String id) {
		String id2 = runtime.getRuntimeType().getId();
		if (id2.indexOf("32") > 0)
			return Messages.target32runtime;
		else if (id2.indexOf("40") > 0)
			return Messages.target40runtime;
		else if (id2.indexOf("41") > 0)
			return Messages.target41runtime;
		else if (id2.indexOf("50") > 0)
			return Messages.target50runtime;
		
		return Messages.target55runtime;
	}

	/**
	 * @see ClasspathRuntimeTargetHandler#resolveClasspathContainer(IRuntime, String)
	 */
	public IClasspathEntry[] resolveClasspathContainer(IRuntime runtime, String id) {
		return resolveClasspathContainer(runtime);
	}

	/**
	 * Resolve the classpath container.
	 */
	protected IClasspathEntry[] resolveClasspathContainer(IRuntime runtime) {
		IPath installPath = runtime.getLocation();
		
		if (installPath == null)
			return new IClasspathEntry[0];
		
		List list = new ArrayList();
		if (runtime.getRuntimeType().getId().indexOf("32") > 0) {
			IPath path = installPath.append("lib");
			addLibraryEntries(list, path.toFile(), true);
		} else {
			IPath path = installPath.append("common");
			addLibraryEntries(list, path.append("lib").toFile(), true);
			addLibraryEntries(list, path.append("endorsed").toFile(), true);
		}
		return (IClasspathEntry[])list.toArray(new IClasspathEntry[list.size()]);
	}
}