/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
/**
 * Helper class to load and save Tomcat server and identify configurations.
 */
public class TomcatServerUtil {
	/**
	 * TomcatServerSerializer constructor comment.
	 */
	protected TomcatServerUtil() {
		super();
	}
	
	/**
	 * Returns the kind of a <code>PackageFragmentRoot</code> from its <code>String</code> form.
	 */
	protected static int getClasspathKindFromString(String kindStr) {
		//if (kindStr.equalsIgnoreCase("prj"))
		//	return IClasspathEntry.CPE_PROJECT;
		if (kindStr.equalsIgnoreCase("var"))
			return IClasspathEntry.CPE_VARIABLE;
		//if (kindStr.equalsIgnoreCase("src"))
		//	return IClasspathEntry.CPE_SOURCE;
		if (kindStr.equalsIgnoreCase("lib"))
			return IClasspathEntry.CPE_LIBRARY;
		return -1;
	}

	/**
	 * Returns a <code>String</code> for the kind of a class path entry.
	 */
	protected static String getClasspathKindToString(int kind) {
		switch (kind) {
			//case IClasspathEntry.CPE_PROJECT :
			//	return "prj";
			//case IClasspathEntry.CPE_SOURCE :
			//	return "src";
			case IClasspathEntry.CPE_LIBRARY :
				return "lib";
			case IClasspathEntry.CPE_VARIABLE :
				return "var";
			default :
				return "unknown";
		}
	}
	
	/**
	 * Create's a classpath entry of the specified kind.
	 *
	 * Returns null if unable to create a valid entry.
	 */
	protected static IClasspathEntry createClasspathEntry(IPath path, int kind, IPath sourceAttachmentPath, IPath sourceAttachmentRootPath) {
		switch (kind) {
			/*case IClasspathEntry.CPE_PROJECT:
				if (!path.isAbsolute())
					return null;
				else
					return JavaCore.newProjectEntry(path);*/
	
			case IClasspathEntry.CPE_LIBRARY:
				if (!path.isAbsolute())
					return null;
				
				return JavaCore.newLibraryEntry(path, sourceAttachmentPath, sourceAttachmentRootPath);
	
			case IClasspathEntry.CPE_VARIABLE:
				return JavaCore.newVariableEntry(path, sourceAttachmentPath, sourceAttachmentRootPath);
	
			default:
				return null;
		}
	}
}