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
 * Represents the kind of a module.
 * <p>
 * The server core framework supports an open-ended set of module kinds,
 * which are contributed via the <code>moduleKinds</code> extension point
 * in the server core plug-in. Module kind objects carry no state
 * (all information is read-only and is supplied by the module kind
 * declaration). The global list of known module kinds is available via
 * {@link ServerCore#getModuleKinds()}. 
 * </p>
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
 * [issue: Equality/identify for module kinds?]
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IModuleKind {
	/**
	 * Returns the id of this module kind.
	 * Each known module kind has a distinct id.
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the module kind id
	 */
	public String getId();

	/**
	 * Returns the displayable name for this module kind.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this module kind
	 */
	public String getName();
}