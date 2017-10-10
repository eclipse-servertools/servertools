/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import junit.framework.TestCase;

import org.eclipse.core.resources.*;
/**
 * Test to scan plugins for missing properties.
 */
public class ScanAction extends TestCase {
	/**
	 * ScanAction constructor comment.
	 */
	public ScanAction() {
		super();
	}

	/* (non-Javadoc)
	 * 
	 */
	public void testIt() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (projects != null) {
			for (IProject project : projects)
				scanProject(project);
		}
	}

	protected void scanProject(IProject project) {
		System.out.println("************************************");
		System.out.println("Project: " + project.getName());
		
		IFile file = project.getFile("plugin.properties");
		if (!file.exists()) {
			System.out.println("  plugin.properties not found");
			return;
		}
		
		//System.out.print("  Reading plugin.properties...");
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = file.getContents();
			props.load(in);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				// ignore
			}
		}
		//System.out.println(props.size() + " found");
		
		List<String> propsFound = new ArrayList<String>();
		List<String> newProps = new ArrayList<String>();
		
		scanContainer(project, props, propsFound, newProps);
		
		System.out.println("Unused properties:");
		if (props.size() == propsFound.size())
			System.out.println("  None");
		else {
			Iterator iterator = props.keySet().iterator();
			while (iterator.hasNext()) {
				String s = (String) iterator.next();
				if (!propsFound.contains(s))
					System.out.println("  " + s);
			}
		}
		
		System.out.println("New properties:");
		if (newProps.size() == 0)
			System.out.println("  None");
		else {
			Iterator iterator = newProps.iterator();
			while (iterator.hasNext()) {
				String s = (String) iterator.next();
				System.out.println("  " + s);
			}
		}
	}
	
	protected void scanContainer(IContainer container, Properties props, List<String> propsFound, List<String> newProps) {
		if (container == null || !container.exists())
			return;
		
		try {
			IResource[] resources = container.members();
			if (resources != null) {
				for (IResource resource: resources) {
					if (resource instanceof IFile) {
						String[] found = scanFile((IFile) resource);
						if (found != null) {
							for (String f : found) {
								if (props.containsKey(f)) {
									if (!propsFound.contains(f))
										propsFound.add(f);
								} else
									newProps.add(f);
							}
						}
					} else if (resource instanceof IContainer) {
						scanContainer((IContainer) resource, props, propsFound, newProps);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Scan a java file and return all of the "%xxx" found.
	 * @param file
	 * @return List of matching strings.
	 */
	protected String[] scanFile(IFile file) {
		if (file == null || !file.exists())
			return new String[0];
		
		if (!file.getName().endsWith("java") && !file.getName().endsWith("xml"))
			return new String[0];

		//System.out.println("  File: " + file.getName());
		List<String> list = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(file.getContents()));
			
			String line = br.readLine();
			while (line != null) {
				int index = line.indexOf("%");
				while (index >= 0) {
					int end = line.indexOf("\"", index + 1);
					if (end >= 0) {
						String s = line.substring(index + 1, end);
						//System.out.println("    Found '" + s + "'");
						list.add(s);
					}
					index = line.indexOf("%", index + 1);
				}
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new String[0];
		}
		
		String[] s = new String[list.size()];
		list.toArray(s);
		return s;
	}
}