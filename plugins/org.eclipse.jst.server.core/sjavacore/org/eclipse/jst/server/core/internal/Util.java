/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.*;

import org.eclipse.jst.server.core.internal.Trace;
/**
 * 
 */
public class Util {
	/**
	 * Returns the types contained within this java element.
	 * @param element org.eclipse.jdt.core.api.IJavaElement
	 * @return org.eclipse.jdt.core.api.IType[]
	 */
	private static IType[] getTypes(IJavaElement element) {
		try {
			if (element.getElementType() != IJavaElement.COMPILATION_UNIT)
				return null;
			
			return ((ICompilationUnit) element).getAllTypes();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * If this resource is a servlet, return the class name.
	 * If not, return null.
	 * @param resource
	 * @return java.lang.String
	 */
	public static String getServletClassName(IResource resource) {
		if (resource == null)
			return null;
	
		try {
			IProject project = resource.getProject();
			IPath path = resource.getFullPath();
			if (!project.hasNature(JavaCore.NATURE_ID) || path == null)
				return null;
	
			IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
			if (!javaProject.isOpen())
				javaProject.open(new NullProgressMonitor());
	
			// output location may not be on classpath
			IPath outputPath = javaProject.getOutputLocation();
			if (outputPath != null && "class".equals(path.getFileExtension()) && outputPath.isPrefixOf(path)) {
				int count = outputPath.segmentCount();
				path = path.removeFirstSegments(count);
			}
	
			// remove initial part of classpath
			IClasspathEntry[] classPathEntry = javaProject.getResolvedClasspath(true);
			if (classPathEntry != null) {
				int size = classPathEntry.length;
				for (int i = 0; i < size; i++) {
					IPath classPath = classPathEntry[i].getPath();
					if (classPath.isPrefixOf(path)) {
						int count = classPath.segmentCount();
						path = path.removeFirstSegments(count);
						i += size;
					}
				}
			}
	
			// get java element
			IJavaElement javaElement = javaProject.findElement(path);
	
			IType[] types = getTypes(javaElement);
			if (types != null) {
				int size2 = types.length;
				for (int i = 0; i < size2; i++) {
					if (isServlet(types[i]))
						return types[i].getFullyQualifiedName();
				}
			}
			return null;
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not find servlet class", e);
			return null;
		}
	}

	/**
	 * Returns true if this java type derives from javax.servlet.GenericServlet
	 * @param type org.eclipse.jdt.core.api.IType
	 * @return boolean
	 */
	private static boolean isServlet(IType type) {
		try {
			ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
			IType[] superClasses = hierarchy.getAllSuperclasses(type);
	
			int size = superClasses.length;
			for (int i = 0; i < size; i++) {
				if ("javax.servlet.GenericServlet".equals(superClasses[i].getFullyQualifiedName()))
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}