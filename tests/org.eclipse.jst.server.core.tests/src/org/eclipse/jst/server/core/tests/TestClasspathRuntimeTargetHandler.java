/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.tests;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.core.ClasspathRuntimeTargetHandler;
import org.eclipse.wst.server.core.IRuntime;
/**
 * 
 */
public class TestClasspathRuntimeTargetHandler extends ClasspathRuntimeTargetHandler{
	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.ClasspathRuntimeTargetHandler#getClasspathContainerLabel(org.eclipse.wst.server.core.IRuntime, java.lang.String)
	 */
	public String getClasspathContainerLabel(IRuntime runtime, String id) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.ClasspathRuntimeTargetHandler#resolveClasspathContainer(org.eclipse.wst.server.core.IRuntime, java.lang.String)
	 */
	public IClasspathEntry[] resolveClasspathContainer(IRuntime runtime, String id) {
		return null;
	}
}