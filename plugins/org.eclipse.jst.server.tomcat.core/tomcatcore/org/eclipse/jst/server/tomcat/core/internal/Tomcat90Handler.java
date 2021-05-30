/**********************************************************************
 * Copyright (c) 2016, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IModule;
/**
 * Tomcat 80 handler.
 */
public class Tomcat90Handler implements ITomcatVersionHandler {
	/**
	 * @see ITomcatVersionHandler#verifyInstallPath(IPath)
	 */
	public IStatus verifyInstallPath(IPath installPath) {
		IStatus result = TomcatVersionHelper.checkCatalinaVersion(installPath, TomcatPlugin.TOMCAT_90);
		// If check was canceled, use folder check
		if (result.getSeverity() == IStatus.CANCEL) {
			result = TomcatPlugin.verifyInstallPathWithFolderCheck(installPath, TomcatPlugin.TOMCAT_90);
		}
		return result;
	}
	
	/**
	 * @see ITomcatVersionHandler#getRuntimeClass()
	 */
	public String getRuntimeClass() {
		return "org.apache.catalina.startup.Bootstrap";
	}
	
	/**
	 * @see ITomcatVersionHandler#getRuntimeClasspath(IPath, IPath)
	 */
	public List getRuntimeClasspath(IPath installPath, IPath configPath) {
		List<IRuntimeClasspathEntry> cp = new ArrayList<IRuntimeClasspathEntry>();
		
		// 9.0 - add bootstrap.jar and tomcat-juli.jar from the Tomcat bin directory
		IPath binPath = installPath.append("bin");
		if (binPath.toFile().exists()) {
			IPath path = binPath.append("bootstrap.jar");
			cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
			// Add commons-daemon.jar if it exists
			path = binPath.append("commons-daemon.jar");
			if (path.toFile().exists()) {
				cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
			}
			// Add tomcat-juli.jar if it exists
			path = binPath.append("tomcat-juli.jar");
			if (path.toFile().exists()) {
				cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
			}
			// If tomcat-juli.jar is not found in the install, check the config directory
			else if (configPath != null){
				path = configPath.append("bin/tomcat-juli.jar");
				if (path.toFile().exists()) {
					cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
				}
			}
		}
		
		return cp;
	}

	/**
	 * @see ITomcatVersionHandler#getRuntimeProgramArguments(IPath, boolean, boolean)
	 */
	public String[] getRuntimeProgramArguments(IPath configPath, boolean debug, boolean starting) {
		List<String> list = new ArrayList<String>();

		if (starting)
			list.add("start");
		else
			list.add("stop");
		
		String[] temp = new String[list.size()];
		list.toArray(temp);
		return temp;
	}

	/**
	 * @see ITomcatVersionHandler#getExcludedRuntimeProgramArguments(boolean, boolean)
	 */
	public String[] getExcludedRuntimeProgramArguments(boolean debug, boolean starting) {
		return null;
	}
	
	/**
	 * @see ITomcatVersionHandler#getRuntimeVMArguments(IPath, IPath, IPath, boolean)
	 */
	public String[] getRuntimeVMArguments(IPath installPath, IPath configPath, IPath deployPath, boolean isTestEnv) {
		return TomcatVersionHelper.getCatalinaVMArguments(installPath, configPath, deployPath, isTestEnv);
	}

	/**
	 * @see ITomcatVersionHandler#getRuntimePolicyFile(IPath)
	 */
	public String getRuntimePolicyFile(IPath configPath) {
		return configPath.append("conf").append("catalina.policy").toOSString();
	}

	/**
	 * @see ITomcatVersionHandler#canAddModule(IModule)
	 */
	public IStatus canAddModule(IModule module) {
		String version = module.getModuleType().getVersion();
		if ("2.2".equals(version) || "2.3".equals(version) || "2.4".equals(version) || "2.5".equals(version)
				|| "3.0".equals(version) || "3.1".equals(version) || "4.0".equals(version))
			return Status.OK_STATUS;
		
		return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorSpec90, null);
	}

	/**
	 * @see ITomcatVersionHandler#getRuntimeBaseDirectory(TomcatServer)
	 */
	public IPath getRuntimeBaseDirectory(TomcatServer server) {
		return TomcatVersionHelper.getStandardBaseDirectory(server);
	}

	/**
	 * @see ITomcatVersionHandler#prepareRuntimeDirectory(IPath)
	 */
	public IStatus prepareRuntimeDirectory(IPath baseDir) {
		return TomcatVersionHelper.createCatalinaInstanceDirectory(baseDir);
	}

	/**
	 * @see ITomcatVersionHandler#prepareDeployDirectory(IPath)
	 */
	public IStatus prepareDeployDirectory(IPath deployPath) {
		return TomcatVersionHelper.createDeploymentDirectory(deployPath,
				TomcatVersionHelper.DEFAULT_WEBXML_SERVLET25);
	}

	/**
	 * @see ITomcatVersionHandler#prepareForServingDirectly(IPath, TomcatServer)
	 */
	public IStatus prepareForServingDirectly(IPath baseDir, TomcatServer server, String tomcatVersion) {
		// Nothing beyond configuration required for Tomcat 9
		return Status.OK_STATUS;
	}

	/**
	 * @see ITomcatVersionHandler#getSharedLoader(IPath)
	 */
	public String getSharedLoader(IPath baseDir) {
		return "common";
	}
	
	/**
	 * Returns true since Tomcat 9.x supports this feature.
	 * 
	 * @return true since feature is supported
	 */
	public boolean supportsServeModulesWithoutPublish() {
		return true;
	}

	/**
	 * @see ITomcatVersionHandler#supportsDebugArgument()
	 */
	public boolean supportsDebugArgument() {
		return false;
	}

	/**
	 * @see ITomcatVersionHandler#supportsSeparateContextFiles()
	 */
	public boolean supportsSeparateContextFiles() {
		return true;
	}

	/**
	 * @see ITomcatVersionHandler#getEndorsedDirectories(IPath)
	 */
	public String getEndorsedDirectories(IPath installPath) {
		return installPath.append("endorsed").toOSString();
	}	
}
