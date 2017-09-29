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
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
/**
 * 
 */
public interface ITomcatVersionHandler {
	/**
	 * Verifies if the specified path points to a a Tomcat
	 * installation of this version.
	 * 
	 * @param installPath an installation path 
	 * @return OK status if a valid installation
	 * exists at the location.  If not valid, the IStatus
	 * contains an indication of why.
	 */
	public IStatus verifyInstallPath(IPath installPath);
	
	/**
	 * Gets the startup class for the Tomcat server.
	 * 
	 * @return server startup class
	 */
	public String getRuntimeClass();
	
	/**
	 * Gets the startup classpath for the Tomcat server.
	 * 
	 * @param installPath an installation path
	 * @return list of classpath entries required to
	 * start the Tomcat server.
	 */
	public List getRuntimeClasspath(IPath installPath, IPath configPath);
	
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
	 * @return array of excluded arguments
	 */
	public String[] getExcludedRuntimeProgramArguments(boolean debug, boolean starting);
	
	/**
	 * Gets the subset of the startup VM arguments for the Tomcat server that apply to all compatible JVM versions.
	 * 
	 * @param installPath installation path for the server
	 * @param configPath configuration path for the server
	 * @param deployPath deploy path for the server
	 * @param isTestEnv test environment flag
	 * @return array of VM arguments for starting the server
	 */
	public String[] getRuntimeVMArguments(IPath installPath, IPath configPath, IPath deployPath, boolean isTestEnv);

	/**
	 * Gets the contents of the Java policy file for the Tomcat server.
	 * 
	 * @param configPath path to configuration
	 * @return contents of Java policy file in the configuration
	 */
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
	 * @param server TomcatServer instance from which to determine
	 * the base path.
	 * @return path to Tomcat instance directory
	 */
	public IPath getRuntimeBaseDirectory(TomcatServer server);

	/**
	 * Prepare server runtime directory. Create catalina instance set of
	 * directories.
	 * 
	 * @param baseDir Tomcat instance directory to prepare
	 * @return result of creation operation 
	 */
	public IStatus prepareRuntimeDirectory(IPath baseDir);
	
	/**
	 * Prepares the specified directory by making sure it exists and is
	 * initialized appropriately.
	 * 
	 * @param deployPath path to the deployment directory
	 *  being prepared
	 * @return status result of the operation
	 */
	public IStatus prepareDeployDirectory(IPath deployPath);
	
	/**
	 * Prepare directory for serving contexts directly if enabled.
	 * If not enabled, restore directory if necessary.
	 * 
	 * @param baseDir path to Tomcat instance directory
	 * @param server TomcatServer instance from which to determine
	 * if serving directly is enabled
	 * @return status result of the operation
	 */
	public IStatus prepareForServingDirectly(IPath baseDir, TomcatServer server, String tomcatVersion);
	
	/**
	 * Gets the name of the "shared" loader to use with serving
	 * modules without publishing.  Returns null if serving modules
	 * without publishing is not supported.
	 * 
	 * @param baseDir path to Tomcat instance directory
	 * @return name of shared loader
	 */
	public String getSharedLoader(IPath baseDir);
	
	/**
	 * Returns true if this server supports serving modules without
	 * publishing.
	 * 
	 * @return true if serving modules without publishing is supported
	 */
	public boolean supportsServeModulesWithoutPublish();
	
	/**
	 * Returns true if this server supports a debug argument. This
	 * argument is expected to affect the level of logging.  Newer
	 * versions of Tomcat use different means of controlling logging
	 * and ignore this argument.
	 * 
	 * @return true if debug argument is supported
	 */
	public boolean supportsDebugArgument();
	
	/**
	 * Returns true if this server supports separate context files.
	 * 
	 * @return true if this server supports separate context files
	 */
	public boolean supportsSeparateContextFiles();

	/**
	 * Returns the endorsed directories derived using the
	 * specified install path.
	 * 
	 * @param installPath installation path for the server
	 * @return The endorsed directories for this server.
	 */
	public String getEndorsedDirectories(IPath installPath);
}
