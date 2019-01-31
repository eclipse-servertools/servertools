/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.core;
/**
 * This interface holds information on the properties of a given project.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @deprecated Project facet support should now be used instead of this API. @see
 *    org.eclipse.wst.common.project.facet.core.IFacetedProject#getRuntime()
 */
public interface IProjectProperties {
	/**
	 * Returns the current runtime target for this project.
	 * 
	 * @return the current runtime target, or <code>null</code> if the project has
	 *    no runtime target
	 * @deprecated Project facet support should now be used instead of this API. @see
	 *    org.eclipse.wst.common.project.facet.core.IFacetedProject#getRuntime()
	 */
	public IRuntime getRuntimeTarget();
}