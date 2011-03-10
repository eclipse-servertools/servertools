/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal.facets;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Messages;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.Trace;
/**
 * Utility class for converting between facet runtimes and server runtimes.
 * <p>
 * This class provides all its functionality through static members.
 * It is not intended to be subclassed or instantiated.
 * </p>
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 */
public final class FacetUtil {
	/**
	 * Static utility class - cannot create an instance.
	 */
	private FacetUtil() {
		// can't create
	}

	/**
	 * Returns the server runtime that corresponds to a facet runtime, or null
	 * if none could be found.
	 * 
	 * @param runtime a facet runtime
	 * @return the server runtime that corresponds to the facet runtime, or
	 *    <code>null</code> if none could be found.
	 */
	public static IRuntime getRuntime(org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime) {
		if (runtime == null)
			throw new IllegalArgumentException();
		
		String id = runtime.getProperty("id");
		if (id == null)
			return null;
		
		IRuntime[] runtimes = ServerCore.getRuntimes();
		for (IRuntime r : runtimes) {
			if (id.equals(r.getId()))
				return r;
		}
		
		return null;
	}

	/**
	 * Returns the facet runtime that corresponds to a server runtime, or null
	 * if none could be found.
	 * 
	 * @param runtime a server runtime
	 * @return the facet runtime that corresponds to the server runtime, or
	 *    <code>null</code> if none could be found.
	 */
	public static org.eclipse.wst.common.project.facet.core.runtime.IRuntime getRuntime(IRuntime runtime) {
		if (runtime == null)
			throw new IllegalArgumentException();
		
		String id = runtime.getId();
		if (id == null)
			return null;
		
		Set runtimes = RuntimeManager.getRuntimes();
		Iterator iterator = runtimes.iterator();
		while (iterator.hasNext()) {
			org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime2 = (org.eclipse.wst.common.project.facet.core.runtime.IRuntime) iterator.next();
			if (id.equals(runtime2.getProperty("id")))
				return runtime2;
		}
		return null;
	}

	/**
	 * Tests whether the facets on a project are supported by a given server. Returns
	 * an OK status if the server's runtime supports the project's facets, and an
	 * ERROR status (with message) if it doesn't.
	 * 
	 * @param project a project
	 * @param server a server
	 * @return OK status if the server's runtime supports the project's facets, and an
	 *    ERROR status (with message) if it doesn't
	 */
	public static final IStatus verifyFacets(IProject project, IServer server) {
		if (server == null)
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorNoRuntime, null);
		IRuntime runtime = server.getRuntime();
		if (runtime == null)
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorNoRuntime, null);
		
		org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime2 = getRuntime(runtime);
		
		if (runtime2 == null) // bug 150194 - what do we do if the facet runtime doesn't exist yet
			return Status.OK_STATUS;
		
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			Iterator iterator = facetedProject.getProjectFacets().iterator();
			while (iterator.hasNext()) {
				IProjectFacetVersion facet = (IProjectFacetVersion) iterator.next();
				if (!runtime2.supports(facet))
					return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorFacet, facet.getProjectFacet().getLabel(), facet.getVersionString()), null);
			}
		} catch (CoreException ce) {
			return ce.getStatus();
		}
		return Status.OK_STATUS;
	}
	
	/**
	 * Returns true if the given runtime is being targeted by the facet
	 * framework, and false otherwise.
	 * 
	 * @param runtime a runtime
	 * @return <code>true</code> if the runtime is in use, and <code>false</code>
	 *    otherwise
	 */
	public static boolean isRuntimeTargeted(IRuntime runtime) {
		try {
			org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime2 = getRuntime(runtime);
			
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			if (projects != null) {
				for (IProject project : projects) {
					IFacetedProject facetedProject = ProjectFacetsManager.create(project);
					if (facetedProject != null) {
						Set<org.eclipse.wst.common.project.facet.core.runtime.IRuntime> set = facetedProject.getTargetedRuntimes();
						if (set != null && set.contains(runtime2))
							return true;
					}
				}
			}
		} catch (Throwable t) {
			Trace.trace(Trace.WARNING, "Could not determine if runtime is in use", t);
		}
		return false;
	}

	/**
	 * Removes the given runtime from the target of all projects.
	 * 
	 * @param runtime a runtime
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *   reporting and cancellation are not desired
	 * @throws CoreException if failed for any reason
	 */
	public static void removeTargets(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		try {
			org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime2 = getRuntime(runtime);
			
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			if (projects != null) {
				for (IProject project : projects) {
					IFacetedProject facetedProject = ProjectFacetsManager.create(project);
					if (facetedProject != null) {
						Set<org.eclipse.wst.common.project.facet.core.runtime.IRuntime> set = facetedProject.getTargetedRuntimes();
						if (set != null && set.contains(runtime2))
							facetedProject.removeTargetedRuntime(runtime2, monitor);
					}
				}
			}
		} catch (CoreException ce) {
			throw ce;
		} catch (Throwable t) {
			Trace.trace(Trace.WARNING, "Could not remove runtime target", t);
		}
	}
}