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
package org.eclipse.jst.server.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

import org.eclipse.wst.server.core.IServer;
/**
 * 
 */
public class LaunchConfigurationUtil {
	/**
	 * Gets the classpath from the launch configuration of the given server.
	 * If create is false, it will return null if there is no launch configuration
	 * (i.e. the server has not been run before) If create is true, it will create
	 * a launch configuration if one does not exist.
	 *
	 * @param server
	 * @param create
	 * @return
	 * @throws CoreException
	 */
	public static IRuntimeClasspathEntry[] getClasspath(IServer server, boolean create, IProgressMonitor monitor) throws CoreException {
		ILaunchConfiguration config = server.getLaunchConfiguration(create, monitor);
		if (config == null)
			return null;
		
		return JavaRuntime.computeUnresolvedRuntimeClasspath(config);
	}

	/**
	 * Sets the classpath on the given server's launch configuration.
	 *
	 * @param server
	 * @param classpath
	 * @throws CoreException
	 */
	public static void setClasspath(IServer server, IRuntimeClasspathEntry[] classpath, IProgressMonitor monitor) throws CoreException {
		ILaunchConfiguration config = server.getLaunchConfiguration(true, monitor);
		ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
	
		List mementos = new ArrayList(classpath.length);
		for (int i = 0; i < classpath.length; i++) {
			IRuntimeClasspathEntry entry = classpath[i];
			mementos.add(entry.getMemento());
		}
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, mementos);
		wc.doSave();
	}
}