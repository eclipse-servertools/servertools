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
package org.eclipse.jst.server.preview.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class Server2 {
	private IPath configPath;
	private Module[] modules;

	public Server2(IPath configPath) {
		this.configPath = configPath;
		init();
	}

	private void init() {
		File f = configPath.toFile();
		if (!f.exists())
			System.err.println("Config doesn't exist at " + configPath.toOSString());
		else {
			try {
				IMemento memento = XMLMemento.loadMemento(f);
				IMemento[] modules2 = memento.getChildren("modules");
				int size = modules2.length;
				List list = new ArrayList(size);
				for (int i = 0; i < size; i++) {
					//String name = modules2[i].getString("name");
					String path = modules2[i].getString("path");
					Module module = new Module(new Path(path));
					list.add(module);
				}
				
				modules = new Module[list.size()];
				list.toArray(modules);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}