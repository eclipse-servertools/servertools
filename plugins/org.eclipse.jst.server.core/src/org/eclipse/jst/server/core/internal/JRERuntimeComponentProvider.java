/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.IJavaRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.internal.Runtime;

public class JRERuntimeComponentProvider extends RuntimeComponentProviderDelegate {
	public static final String CLASSPATH = "classpath";

	protected int timestamp = -1;
	protected IVMInstall vmInstall;
	protected String jvmver;

	public List getRuntimeComponents(IRuntime runtime) {
		// define JRE component
		IJavaRuntime javaRuntime = (IJavaRuntime) runtime.loadAdapter(IJavaRuntime.class, null);
		if (javaRuntime != null) {
			if (timestamp != ((Runtime) runtime).getTimestamp()) {
				vmInstall = null;
				jvmver = null;
				timestamp = ((Runtime) runtime).getTimestamp();
			}
			if (vmInstall == null)
				vmInstall = javaRuntime.getVMInstall(); 
			
			if (jvmver == null) {
				IVMInstall2 vmInstall2 = (IVMInstall2) vmInstall;
				if (vmInstall2 != null)
					jvmver = vmInstall2.getJavaVersion();
			}
			
			String vmInstallName;
			if (vmInstall != null)
				vmInstallName = vmInstall.getName();
			else
				vmInstallName = "Unknown";
			
			IRuntimeComponentVersion rcv = null;
			if (vmInstall == null) {
				// JRE couldn't be found - assume 6.0 for now
				rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("6.0");
			} else if (jvmver == null) {
				Trace.trace(Trace.WARNING, "Could not determine VM version for: " + vmInstallName);
				rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("6.0");
			} else if (jvmver.startsWith("1.3"))
				rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("1.3");
			else if (jvmver.startsWith("1.4"))
				rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("1.4");
			else if (jvmver.startsWith("1.5") || jvmver.startsWith("5.0"))
				rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("5.0");
			else if (jvmver.startsWith("1.6") || jvmver.startsWith("6.0"))
				rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("6.0");
			else {
				Trace.trace(Trace.WARNING, "Invalid Java version: " + vmInstallName + ", " + jvmver);
				rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("6.0");
			}
			
			if (rcv != null) {
				Map properties = new HashMap(3);
				if (vmInstallName != null)
					properties.put("name", vmInstallName);
				else
					properties.put("name", "-");
				
				if (vmInstall == null) {
					// no classpath
				} else if (vmInstall == null || javaRuntime.isUsingDefaultJRE())
					properties.put(CLASSPATH, new Path(JavaRuntime.JRE_CONTAINER).toPortableString());
				else
					properties.put(CLASSPATH, JavaRuntime.newJREContainerPath(vmInstall).toPortableString());
				
				List list = new ArrayList();
				list.add(RuntimeManager.createRuntimeComponent(rcv, properties));
				return list;
			}
		}
		return null;
	}
}