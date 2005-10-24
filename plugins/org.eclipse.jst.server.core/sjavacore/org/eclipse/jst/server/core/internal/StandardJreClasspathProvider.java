/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 *    IBM Corporation - Cleanup
 ******************************************************************************/
package org.eclipse.jst.server.core.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.classpath.IClasspathProvider;
/**
 * 
 */
public final class StandardJreClasspathProvider implements IClasspathProvider {
	private static final IProjectFacet JAVA_FEATURE = ProjectFacetsManager
		.getProjectFacet("jst.java");

	private static final String STANDARD_VM_TYPE = "org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType";

	private final IRuntimeComponent rc;

	public StandardJreClasspathProvider(final IRuntimeComponent rc) {
		this.rc = rc;
	}

	public List getClasspathEntries(final IProjectFacetVersion fv) {
		if (fv.getProjectFacet() == JAVA_FEATURE) {
			IPath path = new Path(JavaRuntime.JRE_CONTAINER);
			path = path.append(STANDARD_VM_TYPE);
			path = path.append(rc.getProperty("name"));

			IClasspathEntry cpentry = JavaCore.newContainerEntry(path);
			return Collections.singletonList(cpentry);
		}

		return null;
	}

	public static final class Factory implements IAdapterFactory {
		private static final Class[] ADAPTER_TYPES = { IClasspathProvider.class };

		public Object getAdapter(final Object adaptable, final Class adapterType) {
			IRuntimeComponent rc = (IRuntimeComponent) adaptable;
			return new StandardJreClasspathProvider(rc);
		}

		public Class[] getAdapterList() {
			return ADAPTER_TYPES;
		}
	}
}