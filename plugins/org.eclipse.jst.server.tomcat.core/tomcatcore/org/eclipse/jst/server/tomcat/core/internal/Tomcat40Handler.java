/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.j2ee.IWebModule;
/**
 * Tomcat 40 handler.
 */
public class Tomcat40Handler implements ITomcatVersionHandler {
	public boolean verifyInstallPath(IPath installPath) {
		if (installPath == null)
			return false;

		String s = installPath.lastSegment();
		if (s != null && s.startsWith("jakarta-tomcat-") && !s.startsWith("jakarta-tomcat-4.0"))
			return false;
		return TomcatPlugin.verifyInstallPath(installPath, TomcatPlugin.TOMCAT_40);
	}
	
	/**
	 * Return the runtime class name.
	 *
	 * @return java.lang.String
	 */
	public String getRuntimeClass() {
		return "org.apache.catalina.startup.Bootstrap";
	}
	
	public List getRuntimeClasspath(IPath installPath) {
		List cp = new ArrayList();
		
		// 4.0 - add bootstrap.jar from the Tomcat bin directory
		IPath binPath = installPath.append("bin");
		if (binPath.toFile().exists()) {
			IPath path = binPath.append("bootstrap.jar");
			cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
		}
		
		return cp;
	}

	/**
	 * Return the program's runtime arguments.
	 *
	 * @return java.lang.String[]
	 */
	public String[] getRuntimeProgramArguments(IPath configPath, boolean debug, boolean starting) {
		List list = new ArrayList();
		if (configPath != null) {
			list.add("-config");
			list.add("\"" + configPath.append("conf").append("server.xml").toOSString() + "\"");
		}
		
		if (debug)
			list.add("-debug");
		
		if (starting)
			list.add("start");
		else
			list.add("stop");
		
		String[] s = new String[list.size()];
		list.toArray(s);
		return s;
	}

	/**
	 * Return the runtime (VM) arguments.
	 *
	 * @return java.lang.String[]
	 */
	public String[] getRuntimeVMArguments(IPath installPath, IPath configPath, boolean isSecure) {
		List list = new ArrayList();
		list.add("-Dcatalina.home=\"" + installPath.toOSString() + "\"");
		String endorsed = installPath.append("bin").toOSString() +
			installPath.append("common").append("lib").toOSString();
		list.add("-Djava.endorsed.dirs=\"" + endorsed + "\"");
		
		// run in secure mode
		if (isSecure) {
			list.add("-Djava.security.manager");
			IPath dir = configPath.append("conf").append("catalina.policy");
			list.add("-Djava.security.policy=\"" + dir.toOSString() + "\"");
		}
		
		String[] s = new String[list.size()];
		list.toArray(s);
		return s;
	}
	
	/**
	 * Returns true if the given project is supported by this
	 * server, and false otherwise.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return boolean
	 */
	public IStatus canAddModule(IWebModule module) {
		if ("1.2".equals(module.getJ2EESpecificationVersion()) || "1.3".equals(module.getJ2EESpecificationVersion()))
			return new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%canAddModule"), null);
		
		return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorSpec40"), null);
	}
}