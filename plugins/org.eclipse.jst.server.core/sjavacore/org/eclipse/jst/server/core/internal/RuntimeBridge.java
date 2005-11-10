/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 *    IBM Corporation - Support for all server types
 ******************************************************************************/
package org.eclipse.jst.server.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.IJavaRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeBridge;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
/**
 * 
 */
public final class RuntimeBridge implements IRuntimeBridge {
	protected static final String CLASSPATH = "classpath";

	private static Map mappings = new HashMap();

	static {
		initialize();
	}

	private static void addMapping(String id, String id2, String version) {
		try {
			mappings.put(id, RuntimeManager.getRuntimeComponentType(id2).getVersion(version));
		} catch (Exception e) {
			// ignore
		}
	}

	private static void initialize() {
		RuntimeFacetMapping[] rfms = JavaServerPlugin.getRuntimeFacetMapping();
		int size = rfms.length;
		for (int i = 0; i < size; i++)
			addMapping(rfms[i].getRuntimeTypeId(), rfms[i].getRuntimeComponent(), rfms[i].getVersion());
		
		// generic runtimes
		addMapping("org.eclipse.jst.server.generic.runtime.weblogic81", "org.eclipse.jst.server.generic.runtime.weblogic", "8.1");
		
		addMapping("org.eclipse.jst.server.generic.runtime.weblogic90", "org.eclipse.jst.server.generic.runtime.weblogic", "9.0");
		
		addMapping("org.eclipse.jst.server.generic.runtime.jboss323", "org.eclipse.jst.server.generic.runtime.jboss", "3.2.3");
		
		addMapping("org.eclipse.jst.server.generic.runtime.jonas4", "org.eclipse.jst.server.generic.runtime.jonas", "4.0");
		
		addMapping("org.eclipse.jst.server.generic.runtime.oracle1013dp4", "org.eclipse.jst.server.generic.runtime.oracle", "1013dp4");
		
		addMapping("org.eclipse.jst.server.generic.runtime.websphere.6", "org.eclipse.jst.server.generic.runtime.websphere", "6.0");
	}

	public void port() {
		// delete old runtimes
		Iterator iterator = RuntimeManager.getRuntimes().iterator();
		while (iterator.hasNext()) {
			org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime =
				(org.eclipse.wst.common.project.facet.core.runtime.IRuntime) iterator.next();
			if ("true".equals(runtime.getProperty("server")))
				RuntimeManager.deleteRuntime(runtime);
		}
		
		// create/bridge existing runtimes
		IRuntime[] runtimes = ServerCore.getRuntimes();
		
		for (int i = 0; i < runtimes.length; i++) {
			IRuntime runtime = runtimes[i];
			String typeId = runtime.getRuntimeType().getId();
			
			//if (!RuntimeManager.isRuntimeDefined(typeId)) {
				IRuntimeComponentVersion mapped = (IRuntimeComponentVersion) mappings.get(typeId);
				
				if (mapped != null) {
					List components = new ArrayList(2);
					String name = runtime.getName();
					
					// define server runtime component
					Map properties = new HashMap();
					properties.put("location", runtime.getLocation().toPortableString());
					properties.put("name", name);
					properties.put("type", runtime.getRuntimeType().getName());
					properties.put("id", runtime.getId());
					
					RuntimeClasspathProviderWrapper rcpw = JavaServerPlugin.findRuntimeClasspathProvider(runtime.getId());
					if (rcpw != null) {
						IPath path = new Path(RuntimeClasspathContainer.SERVER_CONTAINER);
						path = path.append(rcpw.getId()).append(name);
						properties.put(CLASSPATH, path.toPortableString());
					}
					
					components.add(RuntimeManager.createRuntimeComponent(mapped, properties));
					
					// define JRE component
					IJavaRuntime javaRuntime = (IJavaRuntime) runtime.loadAdapter(IJavaRuntime.class, null);
					if (javaRuntime != null) {
						IVMInstall vmInstall = javaRuntime.getVMInstall();
						IVMInstall2 vmInstall2 = (IVMInstall2) vmInstall;
						
						String jvmver = vmInstall2.getJavaVersion();
						IRuntimeComponentVersion rcv;
						
						if (jvmver.startsWith("1.4")) {
							rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("1.4");
						} else if (jvmver.startsWith("1.5")) {
							rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("5.0");
						} else
							continue;
						
						properties = new HashMap();
						properties.put("name", vmInstall.getName());
						IPath path = new Path(JavaRuntime.JRE_CONTAINER);
						path.append(vmInstall.getVMInstallType().getId()).append(vmInstall.getName());
						properties.put(CLASSPATH, path.toPortableString());
						components.add(RuntimeManager.createRuntimeComponent(rcv, properties));
					}
					
					// define facet runtime
					properties = new HashMap();
					properties.put("id", runtime.getId());
					properties.put("server", "true");
					RuntimeManager.defineRuntime(name, components, properties);
				}
			}
		//}
	}
}