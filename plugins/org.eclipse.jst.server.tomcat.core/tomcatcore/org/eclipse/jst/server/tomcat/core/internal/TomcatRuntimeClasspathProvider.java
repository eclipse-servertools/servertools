/*******************************************************************************
 * Copyright (c) 2003, 2024 IBM Corporation and others.
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
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IRuntime;
/**
 * Classpath provider for the Apache Tomcat runtime.
 */
public class TomcatRuntimeClasspathProvider extends RuntimeClasspathProviderDelegate {
	private static final String JST_WEB_FACET_ID = "jst.web";

	private final String getStandardsJavadocLocation(int eeVersion, String jarName) {
		String url = "https://jakarta.ee/specifications/servlet/6.0/apidocs/";
		switch (eeVersion) {
			case 3 :
				url = "https://docs.oracle.com/javaee/3/api/";
				break;
			case 4 :
				url = "https://docs.oracle.com/javaee/4/api/";
				break;
			case 5 :
				url = "https://docs.oracle.com/javaee/5/api/";
				break;
			case 6 :
				url = "https://docs.oracle.com/javaee/6/api/";
				break;
			case 7 :
				url = "https://docs.oracle.com/javaee/7/api/";
				break;
			case 8 :
				url = "https://javaee.github.io/javaee-spec/javadocs/";
				break;
			case 9 :
				// Jakarta EE 9 uses a different URL for each component specification
				url = "https://jakarta.ee/specifications/servlet/5.0/apidocs/";
				if (jarName.contains("jsp")) {
					url = "https://jakarta.ee/specifications/pages/3.0/apidocs/";
				}
				else if (jarName.contains("websocket")) {
					url = "https://jakarta.ee/specifications/websocket/2.0/apidocs/"; // URL doesn't currently work
				}
				else if (jarName.contains("annotation")) {
					url = "https://jakarta.ee/specifications/annotations/2.0/apidocs/jakarta.annotation/"; // URL doesn't currently work
				}
				else if (jarName.equals("el-api.jar")) {
					url = "https://jakarta.ee/specifications/expression-language/4.0/apidocs/";
				}
				else if (jarName.contains("jaspic")) {
					url = "https://jakarta.ee/specifications/authentication/2.0/apidocs/";
				}
				break;
			// Jakarta EE 10 onward use a different URL for each component specification
			case 10:
				url = "https://jakarta.ee/specifications/servlet/6.0/apidocs/";
				if (jarName.contains("jsp")) {
					url = "https://jakarta.ee/specifications/pages/3.1/apidocs/";
				}
				else if (jarName.contains("websocket")) {
					url = "https://jakarta.ee/specifications/websocket/2.1/apidocs/"; // URL doesn't currently work
				}
				else if (jarName.contains("annotation")) {
					// URL doesn't currently work, but possibly just module-summary.html not being treated as an index page?
					url = "https://jakarta.ee/specifications/annotations/2.1/apidocs/jakarta.annotation/";
				}
				else if (jarName.equals("el-api.jar")) {
					url = "https://jakarta.ee/specifications/expression-language/5.0/apidocs/";
				}
				else if (jarName.contains("jaspic")) {
					url = "https://jakarta.ee/specifications/authentication/3.0/apidocs/";
				}
				break;
			case 11:
			default :
				url = "https://jakarta.ee/specifications/servlet/6.1/apidocs/";
				if (jarName.contains("jsp")) {
					url = "https://jakarta.ee/specifications/pages/4.0/apidocs/";
				}
				else if (jarName.contains("websocket")) {
					url = "https://jakarta.ee/specifications/websocket/2.1/apidocs/"; // URL doesn't currently work
				}
				else if (jarName.contains("annotation")) {
					url = "https://jakarta.ee/specifications/annotations/3.0/apidocs/jakarta.annotation/"; // URL doesn't currently work
				}
				else if (jarName.equals("el-api.jar")) {
					url = "https://jakarta.ee/specifications/expression-language/6.0/apidocs/";
				}
				else if (jarName.contains("jaspic")) {
					url = "https://jakarta.ee/specifications/authentication/3.1/apidocs/";
				}
		}

		return url;
	}

	private String getTomcatJavadocLocation(IRuntime runtime) {
		/* Default to v11.0 doc. v9.0 is currently the oldest advertised version on the front page */
		String tomcatDocURL = "http://tomcat.apache.org/tomcat-11.0-doc/api/";
		String runtimeTypeId = runtime.getRuntimeType().getId();
		if (runtimeTypeId.indexOf("101") > 0) {
			tomcatDocURL = "http://tomcat.apache.org/tomcat-10.1-doc/api/";
		}
		else if (runtimeTypeId.indexOf("100") > 0) {
			tomcatDocURL = "http://tomcat.apache.org/tomcat-10.0-doc/api/";
		}
		else if (runtimeTypeId.indexOf("90") > 0) {
			tomcatDocURL = "https://tomcat.apache.org/tomcat-9.0-doc/api/";
		}
		else if (runtimeTypeId.indexOf("85") > 0) {
			tomcatDocURL = "https://tomcat.apache.org/tomcat-8.5-doc/api/";
		}
		else if (runtimeTypeId.indexOf("80") > 0) {
			tomcatDocURL = "https://tomcat.apache.org/tomcat-8.0-doc/api/";
		}
		else if (runtimeTypeId.indexOf("70") > 0) {
			tomcatDocURL = "https://tomcat.apache.org/tomcat-7.0-doc/api/";
		}
		else if (runtimeTypeId.indexOf("60") > 0) {
			tomcatDocURL = "https://tomcat.apache.org/tomcat-6.0-doc/api/";
		}
		return tomcatDocURL;
	}

	/**
	 * @see RuntimeClasspathProviderDelegate#resolveClasspathContainer(IProject, IRuntime)
	 */
	public IClasspathEntry[] resolveClasspathContainer(IProject project, IRuntime runtime) {
		IPath installPath = runtime.getLocation();
		if (installPath == null)
			return new IClasspathEntry[0];

		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		String runtimeId = runtime.getRuntimeType().getId();
		if (runtimeId.indexOf("32") > 0) {
			IPath path = installPath.append("lib");
			addLibraryEntries(list, path.toFile(), true);
		} else if (runtimeId.indexOf("60") > 0 || runtimeId.indexOf("70") > 0 || runtimeId.indexOf("80") > 0 || runtimeId.indexOf("85") > 0
				|| runtimeId.indexOf("90") > 0 || runtimeId.indexOf("100") > 0 || runtimeId.indexOf("101") > 0 || runtimeId.indexOf("110") > 0) {
			// TODO May need some flexibility in case the installation has been configured differently
			// This lib "simplification" may cause issues for some.
			// Not known yet whether packaged Linux installs will go along.
			IPath path = installPath.append("lib");
			addLibraryEntries(list, path.toFile(), true);
		} else {
			IPath path = installPath.append("common");
			addLibraryEntries(list, path.append("lib").toFile(), true);
			addLibraryEntries(list, path.append("endorsed").toFile(), true);
		}

		IClasspathEntry[] entries = list.toArray(new IClasspathEntry[list.size()]);
		String tomcatJavadocLocation = getTomcatJavadocLocation(runtime);

		/*
		 * The URL for the EE API can vary based on the project jst.web facet
		 * version and jar name, or the Tomcat version (preferred, as it is
		 * more concrete). Find the first value, as it is the same for the
		 * entire project. -1 represents unknown.
		 */
		int eeVersion = -1;
		if (runtimeId.indexOf("101") > 0) {
			eeVersion = 10;
		}
		else if (runtimeId.indexOf("100") > 0) {
			eeVersion = 9;
		}
		else if (runtimeId.indexOf("90") > 0) {
			eeVersion = 8;
		}
		else if (runtimeId.indexOf("85") > 0 || runtimeId.indexOf("80") > 0) {
			eeVersion = 7;
		}
		else if (runtimeId.indexOf("70") > 0) {
			eeVersion = 6;
		}
		else if (runtimeId.indexOf("60") > 0) {
			eeVersion = 5;
		}
		else {
			try {
				IFacetedProject faceted = ProjectFacetsManager.create(project);
				if (faceted != null && ProjectFacetsManager.isProjectFacetDefined(JST_WEB_FACET_ID)) {
					IProjectFacet webModuleFacet = ProjectFacetsManager.getProjectFacet(JST_WEB_FACET_ID);
					if (faceted.hasProjectFacet(webModuleFacet)) {
						String servletVersionStr = faceted.getInstalledVersion(webModuleFacet).getVersionString();
						if (servletVersionStr.equals("6.1")) {
							// Jakarta EE 11 is still a work in progress so leave 10 as latest
							eeVersion = 10;
						}
						else if (servletVersionStr.equals("6.0")) {
							eeVersion = 10;
						}
						else if (servletVersionStr.equals("5.0")) {
							eeVersion = 9;
						}
						else if (servletVersionStr.equals("4.0")) {
							eeVersion = 8;
						}
						else if (servletVersionStr.equals("3.1")) {
							eeVersion = 7;
						}
						else if (servletVersionStr.equals("3.0")) {
							eeVersion = 6;
						}
						else if (servletVersionStr.equals("2.5")) {
							eeVersion = 5;
						}
						else if (servletVersionStr.equals("2.4")) {
							eeVersion = 4;
						}
						else if (servletVersionStr.equals("2.3")) {
							eeVersion = 3;
						}
					}
				}
			}
			catch (NumberFormatException e) {
				// default to the latest
				// Jakarta EE 11 is still a work in progress so leave 10 as latest
				eeVersion = 10;
			}
			catch (CoreException e) {
				// default to the latest
				// Jakarta EE 11 is still a work in progress so leave 10 as latest
				eeVersion = 10;
			}
		}

		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			String jarName = entry.getPath().lastSegment();
			if (jarName.endsWith("-api.jar") && !jarName.startsWith("tomcat")) {
				// these are assumed to be the API jars for the EE runtime standards
				IClasspathEntry apiLibraryEntry = JavaCore.newLibraryEntry(entry.getPath(), entry.getSourceAttachmentPath(), entry.getSourceAttachmentRootPath(), entry.getAccessRules(), new IClasspathAttribute[]{JavaCore.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, getStandardsJavadocLocation(eeVersion, entry.getPath().lastSegment()))}, entry.isExported());
				entries[i] = apiLibraryEntry;
			}
			else {
				IClasspathEntry tomcatLibraryEntry = JavaCore.newLibraryEntry(entry.getPath(), entry.getSourceAttachmentPath(), entry.getSourceAttachmentRootPath(), entry.getAccessRules(), new IClasspathAttribute[]{JavaCore.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, tomcatJavadocLocation)}, entry.isExported());
				entries[i] = tomcatLibraryEntry;
			}
		}
		return entries;
	}
}
