/**********************************************************************
 * Copyright (c) 2003, 2005, 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IModule;
/**
 * Tomcat 50 handler.
 */
public class Tomcat50Handler implements ITomcatVersionHandler {
	/**
	 * @see ITomcatVersionHandler#verifyInstallPath(IPath)
	 */
	public IStatus verifyInstallPath(IPath installPath) {
		return TomcatPlugin.verifyInstallPathWithFolderCheck(installPath, TomcatPlugin.TOMCAT_50);
	}
	
	/**
	 * @see ITomcatVersionHandler#getRuntimeClass()
	 */
	public String getRuntimeClass() {
		return "org.apache.catalina.startup.Bootstrap";
	}
	
	/**
	 * @see ITomcatVersionHandler#getRuntimeClasspath(IPath)
	 */
	public List getRuntimeClasspath(IPath installPath) {
		List cp = new ArrayList();
		
		// 5.0 - add bootstrap.jar from the Tomcat bin directory
		IPath binPath = installPath.append("bin");
		if (binPath.toFile().exists()) {
			IPath path = binPath.append("bootstrap.jar");
			cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
		}
		
		return cp;
	}

	/**
	 * @see ITomcatVersionHandler#getRuntimeProgramArguments(IPath, boolean, boolean)
	 */
	public String[] getRuntimeProgramArguments(IPath configPath, boolean debug, boolean starting) {
		List list = new ArrayList();
		
		if (debug)
			list.add("-debug");
		
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
		if (!debug) {
			return new String [] { "-debug" };
		}
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
		if ("2.2".equals(version) || "2.3".equals(version) || "2.4".equals(version))
			return Status.OK_STATUS;
		
		return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorSpec50, null);
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
				TomcatVersionHelper.DEFAULT_WEBXML_SERVLET24);
	}
}