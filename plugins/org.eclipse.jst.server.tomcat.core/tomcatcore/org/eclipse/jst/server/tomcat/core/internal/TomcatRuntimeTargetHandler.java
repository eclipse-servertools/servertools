package org.eclipse.jst.server.tomcat.core.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.ClasspathRuntimeTargetHandler;
import org.eclipse.jst.server.tomcat.core.ITomcatRuntime;

import org.eclipse.wst.server.core.IRuntime;
/**
 * 
 */
public abstract class TomcatRuntimeTargetHandler extends ClasspathRuntimeTargetHandler {
	public IClasspathEntry[] getDelegateClasspathEntries(IRuntime runtime, IProgressMonitor monitor) {
		ITomcatRuntime tomcatRuntime = (ITomcatRuntime) runtime.getExtension(monitor);
		IVMInstall vmInstall = tomcatRuntime.getVMInstall();
		if (vmInstall != null) {
			String name = vmInstall.getName();
			return new IClasspathEntry[] { JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER).append("org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType").append(name)) };
		}
		return null;
	}
	
	public String[] getClasspathEntryIds(IRuntime runtime) {
		return new String[1];
	}

	public String getClasspathContainerLabel(IRuntime runtime, String id) {
		return getLabel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.target.IServerTargetDelegate#getClasspathEntries()
	 */
	public IClasspathEntry[] resolveClasspathContainer(IRuntime runtime, String id) {
		return resolveClasspathContainer(runtime);
	}

	public abstract String getLabel();

	public abstract IClasspathEntry[] resolveClasspathContainer(IRuntime runtime);
}
