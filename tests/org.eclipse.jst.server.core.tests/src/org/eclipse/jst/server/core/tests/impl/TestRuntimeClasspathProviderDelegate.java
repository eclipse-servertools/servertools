/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.tests.impl;

import java.util.*;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.wst.server.core.IRuntime;
/**
 * 
 */
public class TestRuntimeClasspathProviderDelegate extends RuntimeClasspathProviderDelegate {
	public String getClasspathContainerLabel(IRuntime runtime, String id) {
		return null;
	}

	public IClasspathEntry[] resolveClasspathContainer(IRuntime runtime, String id) {
		return null;
	}

	public void testAddMethods() {
		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		try {
			addLibraryEntries(list, null, false);
		} catch (Exception e) {
			// ignore
		}
	}
}
