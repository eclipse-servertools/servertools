/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;

import org.eclipse.wst.server.core.IRuntime;
/**
 * Classpath provider for the Tomcat runtime.
 */
public class TomcatRuntimeClasspathProvider extends RuntimeClasspathProviderDelegate {
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
		} else if (runtimeId.indexOf("60") > 0 || runtimeId.indexOf("70") > 0 || runtimeId.indexOf("80") > 0) {
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
		return list.toArray(new IClasspathEntry[list.size()]);
	}
}
