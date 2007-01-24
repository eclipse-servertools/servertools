/*******************************************************************************
 * Copyright (c) 2003, 2005, 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
/**
 * 
 */
public interface ITomcatVersionHandler {
	public IStatus verifyInstallPath(IPath installPath);
	
	public String getRuntimeClass();
	
	public List getRuntimeClasspath(IPath installPath);
	
	/**
	 * Return the program's runtime arguments.
	 * 
	 * @param configPath a config path
	 * @param debug <code>true</code> if debug mode is on
	 * @param starting <code>true</code> if the server is starting
	 * @return a string array of program arguments
	 */
	public String[] getRuntimeProgramArguments(IPath configPath, boolean debug, boolean starting);

	/**
	 * Arguments that should not appear in the runtime arguments based on
	 * the specified configuration.
	 * 
	 * @param debug <code>true</code> if debug mode is on
	 * @param starting <code>true</code> if the server is starting
	 * @return
	 */
	public String[] getExcludedRuntimeProgramArguments(boolean debug, boolean starting);
	
	public String[] getRuntimeVMArguments(IPath installPath, IPath configPath, boolean isTestEnv);

	public String getRuntimePolicyFile(IPath configPath);

	/**
	 * Returns true if the given project is supported by this
	 * server, and false otherwise.
	 *
	 * @param module a web module
	 * @return the status
	 */
	public IStatus canAddModule(IModule module);

	/**
	 * Returns the runtime base path for relative paths in the server
	 * configuration.
	 * 
	 * @param serverBehaviour TomcatServerBehaviour instance from
	 * which to determine the base path
	 * @return the base path
	 */
	public IPath getRuntimeBaseDirectory(TomcatServerBehaviour serverBehaviour);

	/**
	 * Prepare server runtime directory. Create catalina instance set of
	 * directories.
	 * 
	 * @param baseDir directory at which to prepare the runtime directory.
	 * @return result of creation operation 
	 */
	public IStatus prepareRuntimeDirectory(IPath baseDir);
	
	/**
	 * Prepares the specified directory by making sure it exists and is
	 * initialized appropriately.
	 * 
	 * @param deployPath path to the deployment directory
	 * @return status result of the operation
	 */
	public IStatus prepareDeployDirectory(IPath deployPath);
}