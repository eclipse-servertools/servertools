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
import org.eclipse.jst.common.project.facet.core.IClasspathProvider;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
/**
 * 
 */
public final class StandardJreClasspathProvider implements IClasspathProvider {
	private static final IProjectFacet JAVA_FACET = ProjectFacetsManager.getProjectFacet("jst.java");

	private IRuntimeComponent rc;

	public StandardJreClasspathProvider(IRuntimeComponent rc) {
		this.rc = rc;
	}

	public List getClasspathEntries(IProjectFacetVersion fv) {
		if (fv.getProjectFacet() == JAVA_FACET) {
			String runtimeTypeId = rc.getProperty("type-id");
			String runtimeId = rc.getProperty("id");
			RuntimeClasspathProviderWrapper rcpw = JavaServerPlugin.findRuntimeClasspathProvider(runtimeTypeId);
			if (rcpw != null) {
				IPath path = new Path(RuntimeClasspathContainer.SERVER_CONTAINER);
				path = path.append(rcpw.getId()).append(runtimeId);
				IClasspathEntry cpentry = JavaCore.newContainerEntry(path);
				return Collections.singletonList(cpentry);
			}
		}
		
		return null;
	}

	public static final class Factory implements IAdapterFactory {
		private static final Class[] ADAPTER_TYPES = { IClasspathProvider.class };

		public Object getAdapter(Object adaptable, Class adapterType) {
			IRuntimeComponent rc = (IRuntimeComponent) adaptable;
			return new StandardJreClasspathProvider(rc);
		}

		public Class[] getAdapterList() {
			return ADAPTER_TYPES;
		}
	}
}