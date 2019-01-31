/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal.facets;

import java.util.List;

import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.server.core.IRuntime;
/**
 * A runtime component provider can provide additional runtime components for a facet runtime.
 * This provider is scoped by runtime type and may provide components for multiple
 * runtime instances.
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>internalRuntimeComponentProviders</code> extension point.
 * </p>
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @plannedfor 2.0
 */
public abstract class RuntimeFacetComponentProviderDelegate {
	public RuntimeFacetComponentProviderDelegate() {
		// default constructor
	}

	/**
	 * Add runtime components to the given runtime. Components should be created by calling
	 * RuntimeManager.createRuntimeComponent(IRuntimeComponentVersion, Map)
	 * 
	 * @param runtime a server runtime
	 * @return a list of runtimes, or an empty list or null if there are no additional components
	 */
	public abstract List<IRuntimeComponent> getRuntimeComponents(IRuntime runtime);
}