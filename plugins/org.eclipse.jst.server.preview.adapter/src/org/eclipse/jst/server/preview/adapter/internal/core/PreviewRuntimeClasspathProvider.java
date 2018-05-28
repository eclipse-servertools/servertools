/*******************************************************************************
 * Copyright (c) 2007, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.preview.adapter.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IRuntime;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * 
 */
public class PreviewRuntimeClasspathProvider extends RuntimeClasspathProviderDelegate {
	private static final String[] REQUIRED_BUNDLE_IDS = new String[] {
		getBundleForClass(javax.servlet.ServletContext.class),
		getBundleForClass(javax.servlet.jsp.JspContext.class),
	};

	/**
	 * Gets the symbolic name of the bundle that supplies the given class.
	 */
	private static String getBundleForClass(Class<?> cls) {
		return FrameworkUtil.getBundle(cls).getSymbolicName();
	}

	private String getJavadocLocation(IProject project) {
		int eeVersion = 8;
		try {
			IFacetedProject faceted = ProjectFacetsManager.create(project);
			if (faceted != null && ProjectFacetsManager.isProjectFacetDefined("jst.web")) {
				IProjectFacet webModuleFacet = ProjectFacetsManager.getProjectFacet("jst.web");
				if (faceted.hasProjectFacet(webModuleFacet)) {
					String servletVersionStr = faceted.getInstalledVersion(webModuleFacet).getVersionString();
					if (servletVersionStr.equals("4.0")) {
						eeVersion = 8;
					} else if (servletVersionStr.equals("3.1")) {
						eeVersion = 7;
					} else if (servletVersionStr.equals("3.0")) {
						eeVersion = 6;
					} else if (servletVersionStr.equals("2.5")) {
						eeVersion = 5;
					} else if (servletVersionStr.equals("2.4")) {
						eeVersion = 4;
					} else if (servletVersionStr.equals("2.3")) {
						eeVersion = 3;
					}
				}
			}
		}
		catch (NumberFormatException e) {
			// default to the latest
		}
		catch (CoreException e) {
			// default to the latest
		}

		String url = "https://javaee.github.io/javaee-spec/javadocs/";
		switch (eeVersion) {
		case 3:
			url = "https://docs.oracle.com/javaee/3/api/";
			break;
		case 4:
			url = "https://docs.oracle.com/javaee/4/api/";
			break;
		case 5:
			url = "https://docs.oracle.com/javaee/5/api/";
			break;
		case 6:
			url = "https://docs.oracle.com/javaee/6/api/";
			break;
		case 7:
			url = "https://docs.oracle.com/javaee/7/api/";
			break;
		case 8:
			url = "https://javaee.github.io/javaee-spec/javadocs/";
			break;
		default:
			url = "https://javaee.github.io/javaee-spec/javadocs/";
			break;
		}
		return url;
	}

	@Override
	public IClasspathEntry[] resolveClasspathContainer(IProject project, IRuntime runtime) {
		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		String docUrl = getJavadocLocation(project);

		int size = REQUIRED_BUNDLE_IDS.length;
		for (int i = 0; i < size; i++) {
			Bundle b = Platform.getBundle(REQUIRED_BUNDLE_IDS[i]);
			IPath path = PreviewRuntime.getJarredPluginPath(b);
			if (path != null) {
				IClasspathEntry libraryEntry = JavaCore.newLibraryEntry(path, null, null, new IAccessRule[0], new IClasspathAttribute[]{JavaCore.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, docUrl)}, false);
				list.add(libraryEntry);
			}
		}

		return list.toArray(new IClasspathEntry[list.size()]);
	}
}