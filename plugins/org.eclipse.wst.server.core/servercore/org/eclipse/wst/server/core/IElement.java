/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
/**
 * Provides common methods for working with elements that are
 * modified via working copies. This interface provides the
 * getters; the setters are on {@link IElementWorkingCopy}.
 * <p>
 * [issue: Why are attributes exposed? The attribute ids and
 * values are passed to property change listeners. However,
 * they are not useful unless there is a spec'd correlation
 * between methods like getName and an attribute "name". The
 * constants are declared on Base, which is internal.]
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 1.0
 */
public interface IElement {
	/**
	 * Returns the displayable name for this element.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name
	 */
	public String getName();
	
	/**
	 * Returns the id of this element.
	 * Each element (of a given type) has a distinct id, fixed for
	 * its lifetime. Ids are intended to be used internally as keys;
	 * they are not intended to be shown to end users.
	 * 
	 * @return the element id
	 */
	public String getId();

	/**
	 * Deletes the persistent representation of this element.
	 * <p>
	 * [issue: This method is out of place. Elements do not
	 * have a notion of being connected to an underlying file.]
	 * </p>
	 * 
	 * @throws CoreException [missing]
	 */
	public void delete() throws CoreException;

	/**
	 * Returns whether this element is locked.
	 * When an element is locked, the user cannot make changes to it.
	 * <p>
	 * [issue: It's odd to have this at the API level because
	 * it has no force. Once would modify a locked element as
	 * readily as an unlocked one, via a working copy.
	 * This facility, which is unused, needs to be motivated
	 * (or removed).]
	 * </p>
	 *
	 * @return <code>true</code> if this element is locked,
	 * and <code>false</code> otherwise
	 */
	public boolean isLocked();

	/**
	 * Returns true if this element is private (not shown in the UI).
	 * 
	 * @return boolean
	 */
	public boolean isPrivate();

	/**
	 * Returns true if this is a working copy.
	 * 
	 * @return boolean
	 */
	public boolean isWorkingCopy();
	
	/**
	 * Returns true if there are working copies that have not been saved or released.
	 * 
	 * @return boolean
	 */
	public boolean isWorkingCopiesExist();
	
	/**
	 * Returns true if there is a working copy that is dirty.
	 * 
	 * @return boolean
	 */
	public boolean isAWorkingCopyDirty();

	/**
	 * Returns true if the plugin containing the delegate is loaded.
	 * 
	 * @return boolean
	 */
	public boolean isDelegatePluginActivated();
	
	/**
	 * Returns true if the delegate has been loaded.
	 * 
	 * @return
	 */
	public boolean isDelegateLoaded();

	public int getAttribute(String attributeName, int defaultValue);

	public boolean getAttribute(String attributeName, boolean defaultValue);
	
	public String getAttribute(String attributeName, String defaultValue);

	public List getAttribute(String attributeName, List defaultValue);

	public Map getAttribute(String attributeName, Map defaultValue);

	public IStatus validateEdit(Object context);
	
	public int getTimestamp();
}