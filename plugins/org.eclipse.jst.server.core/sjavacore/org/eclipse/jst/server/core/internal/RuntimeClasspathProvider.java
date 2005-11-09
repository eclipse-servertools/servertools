/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 *    IBM Corporation - Support for all server types
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
public final class RuntimeClasspathProvider implements IClasspathProvider {
	private static final IProjectFacet WEB_FACET = ProjectFacetsManager.getProjectFacet("jst.web");
	private static final IProjectFacet EJB_FACET = ProjectFacetsManager.getProjectFacet("jst.ejb");
	private static final IProjectFacet EAR_FACET = ProjectFacetsManager.getProjectFacet("jst.ear");
	private static final IProjectFacet UTILITY_FACET = ProjectFacetsManager.getProjectFacet("jst.utility");
	private static final IProjectFacet CONNECTOR_FACET = ProjectFacetsManager.getProjectFacet("jst.connector");

	private final IRuntimeComponent rc;

	public RuntimeClasspathProvider(final IRuntimeComponent rc) {
		this.rc = rc;
	}

	public List getClasspathEntries(final IProjectFacetVersion fv) {
		if (fv.getProjectFacet() == WEB_FACET || fv.getProjectFacet() == EJB_FACET ||
				fv.getProjectFacet() == EAR_FACET || fv.getProjectFacet() == UTILITY_FACET ||
				fv.getProjectFacet() == CONNECTOR_FACET) {
			IPath path = new Path(RuntimeClasspathContainer.SERVER_CONTAINER);
			if (rc.getRuntimeComponentType().getId().indexOf("tomcat") < 0)
				path = path.append("org.eclipse.jst.server.generic.runtimeTarget");
			else
				path = path.append("org.eclipse.jst.server.tomcat.runtimeTarget");
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
			return new RuntimeClasspathProvider(rc);
		}

		public Class[] getAdapterList() {
			return ADAPTER_TYPES;
		}
	}
}