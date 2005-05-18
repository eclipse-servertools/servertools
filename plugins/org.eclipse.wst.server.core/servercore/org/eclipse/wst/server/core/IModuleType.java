/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;
/**
 * Represents the type of a module.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @since 1.0
 */
public interface IModuleType {
	/**
	 * Returns the module type id.
	 * <p>
	 * The module type id is a "." separated string uniquely identifying the
	 * type of module. Like a java package name, it should scope the type from
	 * most general to specific. For instance, "j2ee.web".
	 * </p>
	 * 
	 * @return the module type id
	 */
	public String getId();

	/**
	 * Returns the displayable name for this module type.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this module type
	 */
	public String getName();

	/**
	 * Returns the version (specification level) of this module type,
	 * e.g. "1.0" or "1.3.2a".
	 * <p>
	 * The version will normally be a series of numbers separated by
	 * ".", but it could be different depending on the type of module.
	 * For module types where a version does not make sense (e.g.
	 * a static Web module), <code>null</code> is returned.
	 * </p>
	 * 
	 * @return the specification version of this module type, or
	 *    <code>null</code> if there is no version 
	 */
	public String getVersion();
}