/*******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel R. Somerfield - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal.cactus;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jst.server.core.internal.Trace;
/**
 * 
 */
public class ServletTestCasePropertyTester extends PropertyTester {
	public static final String PROPERTY_IS_SERVLET_TEST_CASE = "isServletTestCase";
	private static final String TEST_SUPERCLASS_NAME = "org.apache.cactus.ServletTestCase";

	public boolean test(Object receiver, String method, Object[] args, Object expectedValue) {
		IJavaElement javaElement = null;
		if (receiver instanceof IAdaptable) {
			javaElement = (IJavaElement) ((IAdaptable) receiver).getAdapter(IJavaElement.class);
		}
		if (javaElement != null) {
			if (!javaElement.exists()) {
				return false;
			}
		}
		if (javaElement instanceof IJavaProject
				|| javaElement instanceof IPackageFragmentRoot
				|| javaElement instanceof IPackageFragment) {
			return true;
		}
		if (javaElement != null) {
			if (PROPERTY_IS_SERVLET_TEST_CASE.equals(method)) { //$NON-NLS-1$
				return isServletTestCase(javaElement);
			}
		}
		return false;
	}

	private boolean isServletTestCase(IJavaElement element) {
		IType testType = null;

		try {
			if (element instanceof ICompilationUnit) {
				ICompilationUnit cu = (ICompilationUnit) element;
				testType = cu.getType(Signature.getQualifier(cu.getElementName()));
			} else if (element instanceof IClassFile) {
				testType = ((IClassFile) element).getType();
			} else if (element instanceof IType) {
				testType = (IType) element;
			} else if (element instanceof IMember) {
				testType = ((IMember) element).getDeclaringType();
			}

			if (testType != null && testType.exists() && isTestOrSuite(testType)) {
				return true;
			}
		} catch (JavaModelException e) {
			// TODO: what do we do here?
			Trace.trace(Trace.SEVERE, "Error checking isServlet", e);
		}
		return false;
	}

	/**
	 * 
	 * @param testType
	 * @return true if the type is a test, false otherwise
	 */
	private boolean isTestOrSuite(IType testType) throws JavaModelException {
		IType[] types = testType.newSupertypeHierarchy(null).getAllSuperclasses(testType);
		for (int i = 0; i < types.length; i++) {
			if (types[i].getFullyQualifiedName().equals(TEST_SUPERCLASS_NAME)) {
				return true;
			}
		}
		return false;
	}
}