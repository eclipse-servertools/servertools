/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
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
 * An element.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IElement {
	/**
	 * Return the label (name) of this element.
	 * 
	 * @return java.lang.String
	 */
	public String getName();
	
	/**
	 * Return the id of this element.
	 * 
	 * @return java.lang.String
	 */
	public String getId();

	public void delete() throws CoreException;

	/**
	 * Returns true if this element is locked. (user cannot make changes).
	 *
	 * @return boolean
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