/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.wst.server.core.internal.facets.RuntimeFacetComponentProviderDelegate;
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
public abstract class RuntimeComponentProviderDelegate extends RuntimeFacetComponentProviderDelegate {
	// nothing added
}