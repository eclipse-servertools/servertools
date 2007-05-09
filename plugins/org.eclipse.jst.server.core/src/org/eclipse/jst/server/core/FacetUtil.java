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
package org.eclipse.jst.server.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
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
		return org.eclipse.wst.server.core.internal.facets.FacetUtil.getRuntime(runtime);
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
		return org.eclipse.wst.server.core.internal.facets.FacetUtil.getRuntime(runtime);
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
		return org.eclipse.wst.server.core.internal.facets.FacetUtil.verifyFacets(project, server);
	}
}