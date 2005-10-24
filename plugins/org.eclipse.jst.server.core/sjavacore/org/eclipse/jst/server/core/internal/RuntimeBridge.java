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
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
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
	private static Map mappings = new HashMap();

	static {
		mappings.put("org.eclipse.jst.server.tomcat.runtime.32", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.tomcat").getVersion("3.2"));
		
		mappings.put("org.eclipse.jst.server.tomcat.runtime.40", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.tomcat").getVersion("4.0"));
		
		mappings.put("org.eclipse.jst.server.tomcat.runtime.41", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.tomcat").getVersion("4.1"));
		
		mappings.put("org.eclipse.jst.server.tomcat.runtime.50", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.tomcat").getVersion("5.0"));
		
		mappings.put("org.eclipse.jst.server.tomcat.runtime.55", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.tomcat").getVersion("5.5"));
		
		// generic runtimes
		mappings.put("org.eclipse.jst.server.generic.runtime.weblogic81", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.generic.runtime.weblogic").getVersion("8.1"));
		
		mappings.put("org.eclipse.jst.server.generic.runtime.weblogic90", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.generic.runtime.weblogic").getVersion("9.0"));
		
		mappings.put("org.eclipse.jst.server.generic.runtime.jboss323", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.generic.runtime.jboss").getVersion("3.2.3"));
		
		mappings.put("org.eclipse.jst.server.generic.runtime.jonas4", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.generic.runtime.jonas").getVersion("4.0"));
		
		mappings.put("org.eclipse.jst.server.generic.runtime.oracle1013dp4", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.generic.runtime.oracle").getVersion("1013dp4"));
		
		mappings.put("org.eclipse.jst.server.generic.runtime.websphere.6", RuntimeManager
				.getRuntimeComponentType("org.eclipse.jst.server.generic.runtime.websphere").getVersion("6.0"));
	}

	public void port() {
		IRuntime[] runtimes = ServerCore.getRuntimes();
		
		for (int i = 0; i < runtimes.length; i++) {
			IRuntime runtime = runtimes[i];
			String typeId = runtime.getRuntimeType().getId();
			
			if (!RuntimeManager.isRuntimeDefined(typeId)) {
				IRuntimeComponentVersion mapped = (IRuntimeComponentVersion) mappings.get(typeId);
				
				if (mapped != null) {
					List components = new ArrayList(2);
					String name = runtime.getName();
					
					// define server runtime component
					Map properties = new HashMap();
					properties.put("location", runtime.getLocation().toPortableString());
					properties.put("name", name);
					properties.put("id", runtime.getId());
					components.add(RuntimeManager.createRuntimeComponent(mapped, properties));
					
					// define JRE component
					IJavaRuntime gr = (IJavaRuntime) runtime.loadAdapter(IJavaRuntime.class, null);
					IVMInstall vmInstall = gr.getVMInstall();
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
					components.add(RuntimeManager.createRuntimeComponent(rcv, properties));
					
					// define facet runtime
					properties = new HashMap();
					properties.put("id", runtime.getId());
					RuntimeManager.defineRuntime(name, components, properties);
				}
			}
		}
	}
}