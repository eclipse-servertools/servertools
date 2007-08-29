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
package org.eclipse.jst.server.preview.adapter.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;

import org.eclipse.wst.server.core.IRuntime;
import org.osgi.framework.Bundle;
/**
 * 
 */
public class PreviewRuntimeClasspathProvider extends RuntimeClasspathProviderDelegate {
	private static final String[] REQUIRED_BUNDLE_IDS = new String[] {
		"javax.servlet",
		"javax.servlet.jsp"
	};

	/** (non-Javadoc)
	 * @see RuntimeClasspathProviderDelegate#resolveClasspathContainer(IProject, IRuntime)
	 */
	public IClasspathEntry[] resolveClasspathContainer(IProject project, IRuntime runtime) {
		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		
		int size = REQUIRED_BUNDLE_IDS.length;
		for (int i = 0; i < size; i++) {
			Bundle b = Platform.getBundle(REQUIRED_BUNDLE_IDS[i]);
			IPath path = PreviewRuntime.getJarredPluginPath(b);
			if (path != null)
				list.add(JavaCore.newLibraryEntry(path, null, null));
		}
		
		return list.toArray(new IClasspathEntry[list.size()]);
	}
}