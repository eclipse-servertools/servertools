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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
/**
 * 
 */
public class Module {
	private String name;
	private boolean isStatic;
	private String context;
	private String projectPath;
	protected String contextRoot;
	private Servlet[] servlets;
	protected IPath[] outputPaths;

	public Module(String name, boolean isStatic, String context, String projectPath) {
		this.name = name;
		this.isStatic = isStatic;
		this.context = context;
		this.projectPath = projectPath;
	}

	public String getName() {
		return name;
	}

	public boolean isStaticWeb() {
		return isStatic;
	}

	public String getContext() {
		return context;
	}

	public String getPath() {
		return projectPath;
	}

	public void initialize() {
		loadComponentFile();
		loadWebDD();
	}

	private void loadWebDD() {
		File f = findWebDD().toFile();
		
		if (!f.exists()) {
			System.err.println("Could not find web.xml");
			return;
		}
		
		try {
			IMemento memento = XMLMemento.loadMemento(f);
			
			IMemento[] servlets2 = memento.getChildren("servlet");
			int size = servlets.length;
			List<Servlet> list = new ArrayList<Servlet>(size);
			for (int i = 0; i < size; i++) {
				Servlet s = new Servlet();
				IMemento m = servlets2[i].getChild("servlet-name");
				s.name = m.getName().trim();
				m = servlets2[i].getChild("servlet-class");
				s.name = m.getName().trim();
			}
			
			IMemento[] mappings = memento.getChildren("servlet-mapping");
			size = mappings.length;
			for (int i = 0; i < size; i++) {
				IMemento m = mappings[i].getChild("servlet-name");
				Servlet s = null;
				m = mappings[i].getChild("url-pattern");
				s.urlPattern = m.getName().trim();
			}
			
			servlets = new Servlet[list.size()];
			list.toArray(servlets);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadComponentFile() {
		File f = new Path(projectPath).append(".settings").append(".component").toFile();
		
		if (!f.exists()) {
			System.err.println("Could not find component file");
			return;
		}
		
		try {
			IMemento memento = XMLMemento.loadMemento(f);
			
			// project-modules
			// wb-module
			// <wb-resource deploy-path="/" source-path="/WebContent"/>
			// <property name="context-root" value="UKWeb"/>
			// <property name="java-output-path" value="/build/classes/"/>
			/*
	        <dependent-module deploy-path="/WEB-INF/lib" handle="module:/resource/MyUtils/MyUtils">
	            <dependency-type>uses</dependency-type>
	        </dependent-module>
	        */
			
			IMemento module = memento.getChild("wb-module");
			
			IMemento[] resources = module.getChildren("wb-resource");
			int size = resources.length;
			//List list = new ArrayList(size);
			for (int i = 0; i < size; i++) {
				// TODO
			}
			
			IMemento[] dependent = module.getChildren("dependent-module");
			size = dependent.length;
			//List list = new ArrayList(size);
			for (int i = 0; i < size; i++) {
				// TODO
			}
			
			// get context root
			IMemento[] properties = module.getChildren("property");
			size = properties.length;
			for (int i = 0; i < size; i++) {
				String name2 = properties[i].getString("name");
				if ("context-root".equals(name2))
					contextRoot = properties[i].getString("value");
				else if ("java-output-path".equals(name2))
					outputPaths = new IPath[] { new Path(projectPath).append(properties[i].getString("value")) };
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private IPath findWebDD() {
		int size = outputPaths.length;
		for (int i = 0; i < size; i++) {
			IPath path = outputPaths[i].append("WEB-INF").append("web.xml");
			if (path.toFile().exists())
				return path;
		}
		return null;
	}

	public ClassLoader getClassloader() {
		File f = new Path(projectPath).append("config").toFile();
		
		if (!f.exists()) {
			System.err.println("Config doesn't exist at " + projectPath);
			return null;
		}
		
		try {
			IMemento memento = XMLMemento.loadMemento(f);
			memento.getChildren("modules");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}