/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/
package org.eclipse.jst.server.core.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.classpath.IClasspathProvider;
/**
 * 
 */
public final class TomcatClasspathProvider implements IClasspathProvider {
	private static final IProjectFacet WEB_FEATURE = ProjectFacetsManager.getProjectFacet("jst.web");

	private final IRuntimeComponent rc;

	public TomcatClasspathProvider(final IRuntimeComponent rc) {
		this.rc = rc;
	}

	public List getClasspathEntries(final IProjectFacetVersion fv) {
		if (fv.getProjectFacet() == WEB_FEATURE) {
			IPath path = new Path(RuntimeClasspathContainer.SERVER_CONTAINER + "/org.eclipse.jst.server.tomcat.runtimeTarget");
			path = path.append(rc.getProperty("name"));
			
			final IClasspathEntry cpentry = JavaCore.newContainerEntry(path);
			
			return Collections.singletonList(cpentry);
		}
		
		return null;
	}

	public static final class Factory implements IAdapterFactory {
		private static final Class[] ADAPTER_TYPES = { IClasspathProvider.class };

		public Object getAdapter(final Object adaptable, final Class adapterType) {
			final IRuntimeComponent rc = (IRuntimeComponent) adaptable;
			return new TomcatClasspathProvider(rc);
		}

		public Class[] getAdapterList() {
			return ADAPTER_TYPES;
		}
	}
}