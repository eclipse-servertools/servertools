/*******************************************************************************
 * Copyright (c) 2007, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerConfig {
	private String configPath;
	private Module[] modules;
	private int port = 8080;

	public ServerConfig(String configPath) {
		this.configPath = configPath;
		init();
	}

	private void init() {
		File f = new File(configPath);
		if (!f.exists())
			System.err.println("Config doesn't exist at " + configPath);
		else {
			if (!f.isDirectory()) {
				try {
					IMemento memento = XMLMemento.loadMemento(f);
					Integer prt = memento.getInteger("port");
					if (prt != null)
						port = prt.intValue();
					
					IMemento[] modules2 = memento.getChildren("module");
					int size = modules2.length;
					List<Module> list = new ArrayList<Module>(size);
					for (IMemento mod : modules2) {
						String name = mod.getString("name");
						boolean isStatic = "static".equals(mod.getString("type"));
						String path = mod.getString("path");
						String context = mod.getString("context");
						if (context != null && !context.startsWith("/")) {
							context = "/" + context;
						}
						Module module = new Module(name, isStatic, context, path);
						list.add(module);
					}
					
					modules = new Module[list.size()];
					list.toArray(modules);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				// mostly for use during development
				modules = new Module[1];
				modules[0] = new Module(f.getName(), false, "/" + f.getName(), f.getPath());
			}
		}
	}

	public int getPort() {
		return port;
	}

	public Module[] getModules() {
		return modules;
	}
}