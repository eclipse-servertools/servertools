/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
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
	}

	public void port() {
		final IRuntime[] runtimes = ServerCore.getRuntimes();

		for (int i = 0; i < runtimes.length; i++) {
			final IRuntime runtime = runtimes[i];
			final String name = runtime.getName();

			if (!RuntimeManager.isRuntimeDefined(name)) {
				final String type = runtime.getRuntimeType().getId();
				final IRuntimeComponentVersion mapped = (IRuntimeComponentVersion)
						mappings.get(type);
				
				if (mapped != null) {
					final List components = new ArrayList();
					
					Map properties;
					
					properties = new HashMap();
					properties.put("location", runtime.getLocation().toPortableString());
					properties.put("name", name);
					
					components.add(RuntimeManager.createRuntimeComponent(mapped, properties));
					
					IJavaRuntime gr = (IJavaRuntime) runtime.loadAdapter(IJavaRuntime.class, null);
					IVMInstall vmInstall = gr.getVMInstall();
					IVMInstall2 vmInstall2 = (IVMInstall2) vmInstall;
					
					final String jvmver = vmInstall2.getJavaVersion();
					final IRuntimeComponentVersion rcv;
					
					if (jvmver.startsWith("1.4")) {
						rcv = RuntimeManager.getRuntimeComponentType("standard.jre")
								.getVersion("1.4");
					} else if (jvmver.startsWith("1.5")) {
						rcv = RuntimeManager.getRuntimeComponentType("standard.jre")
								.getVersion("5.0");
					} else {
						continue;
					}

					properties = new HashMap();
					properties.put("name", vmInstall.getName());
					components.add(RuntimeManager.createRuntimeComponent(rcv, properties));

					RuntimeManager.defineRuntime(name, components, null);
				}
			}
		}
	}
}