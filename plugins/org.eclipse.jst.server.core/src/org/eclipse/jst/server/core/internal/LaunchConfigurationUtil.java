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
package org.eclipse.jst.server.core.internal;
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
	 * @param monitor a progress monitor
	 * @return an array containing runtime classpath entries
	 * @throws CoreException
	 */
	/*public static IRuntimeClasspathEntry[] getClasspath(IServer server, boolean create, IProgressMonitor monitor) throws CoreException {
		ILaunchConfiguration config = server.getLaunchConfiguration(create, monitor);
		if (config == null)
			return null;
		
		return JavaRuntime.computeUnresolvedRuntimeClasspath(config);
	}*/

	/**
	 * Sets the classpath on the given server's launch configuration.
	 *
	 * @param server
	 * @param classpath
	 * @throws CoreException
	 */
	/*public static void setClasspath(IServer server, IRuntimeClasspathEntry[] classpath, IProgressMonitor monitor) throws CoreException {
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
	}*/
}
