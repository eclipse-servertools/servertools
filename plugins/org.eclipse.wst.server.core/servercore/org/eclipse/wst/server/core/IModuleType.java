/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;
/**
 * Represents the type of a module.
 * <p>
 * [issue: It's confusing to have a type named IModuleType as well.
 * The terminology should be "module types", to make it consistent with
 * server types, etc. On that reading, this interface would be named
 * IModuleType, and the existing one something else (if still needed).]
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IModuleType {
	/**
	 * Returns the module type id.
	 * <p>
	 * [issue: Are these strings "module kind ids"? I.e., the same as those returned by 
	 * IModuleType.getId()?]
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
	 * Returns the version (spec level), e.g., "1.0", "1.3.2".
	 * <p>
	 * [issue: This notion of a module type "version" appears here.
	 * There is no counterpart elsewhere (and certainly not in
	 * IModuleType). The phrase "spec level" suggests something
	 * a little more J2EE-centric (what would be the spec level for
	 * a static html web module?) It feels like this should be folded
	 * in to the module type/kind id.]
	 * </p>
	 * <p>
	 * [issue: Spec format of version string?]
	 * </p>
	 * 
	 * @return the version
	 */
	public String getVersion();
}