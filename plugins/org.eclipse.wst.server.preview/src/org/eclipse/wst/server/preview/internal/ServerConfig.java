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
			try {
				IMemento memento = XMLMemento.loadMemento(f);
				Integer prt = memento.getInteger("port");
				if (prt != null)
					port = prt.intValue();
				
				IMemento[] modules2 = memento.getChildren("module");
				int size = modules2.length;
				List list = new ArrayList(size);
				for (int i = 0; i < size; i++) {
					String name = modules2[i].getString("name");
					boolean isStatic = "static".equals(modules2[i].getString("type"));
					String path = modules2[i].getString("path");
					String context = modules2[i].getString("context");
					Module module = new Module(name, isStatic, context, path);
					list.add(module);
				}
				
				modules = new Module[list.size()];
				list.toArray(modules);
			} catch (Exception e) {
				e.printStackTrace();
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