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

import java.io.File;
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

	private Map vmCache = new HashMap();

	class VMInstallCache {
		// cached attributes
		IVMInstall vmInstall;
		String jvmver;
		
		// caching validation
		int timestamp;
		File location;
	}

	public List getRuntimeComponents(IRuntime runtime) {
		// define JRE component
		IJavaRuntime javaRuntime = (IJavaRuntime) runtime.loadAdapter(IJavaRuntime.class, null);
		if (javaRuntime != null) {
			VMInstallCache cache = (VMInstallCache) vmCache.get(runtime.getId());
			if (cache != null) {
				if (cache.timestamp != ((Runtime) runtime).getTimestamp())
					cache = null;
				if (cache != null && cache.location != null && cache.vmInstall != null && !cache.location.equals(cache.vmInstall.getInstallLocation()))
					cache = null;
			}
			
			if (cache == null) {
				cache = new VMInstallCache();
				cache.timestamp = ((Runtime) runtime).getTimestamp();
				cache.vmInstall = javaRuntime.getVMInstall();
				
				if (cache.vmInstall != null) {
					if (cache.vmInstall instanceof IVMInstall2) {
						IVMInstall2 vmInstall2 = (IVMInstall2) cache.vmInstall;
						if (vmInstall2 != null)
							cache.jvmver = vmInstall2.getJavaVersion();
					}
					cache.location = cache.vmInstall.getInstallLocation();
				}
				vmCache.put(runtime.getId(), cache);
			}
			
			IVMInstall vmInstall = cache.vmInstall;
			String jvmver = cache.jvmver;
			
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
				String name = "-";
				if (vmInstallName != null)
					name = vmInstallName;
				properties.put("name", name);
				
				StringBuffer buf = new StringBuffer();
				buf.append("JRE ");
				buf.append(rcv.getVersionString());
				buf.append(": ");
				buf.append(name);
				properties.put("type", buf.toString());
				
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