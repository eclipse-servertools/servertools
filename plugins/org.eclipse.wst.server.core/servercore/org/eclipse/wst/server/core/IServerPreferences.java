/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
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
 * Helper class that stores preference information for server core.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerPreferences {
	/**
	 * Returns whether servers will be automatically restarted when
	 * required.
	 *
	 * @return boolean
	 */
	public boolean isAutoRestarting();

	/**
	 * Returns whether servers will be automatically restarted when
	 * required.
	 *
	 * @return boolean
	 */
	public boolean isDefaultAutoRestarting();

	/**
	 * Set whether servers will be automatically restarted when
	 * they need a restart.
	 *
	 * @param boolean
	 */
	public void setAutoRestarting(boolean b);

	/**
	 * Returns whether publishing should automatically occur when necessary.
	 *
	 * @return boolean
	 */
	public boolean isAutoPublishing();
	
	/**
	 * Returns the default setting of whether publishing should automatically
	 * occur when necessary.
	 *
	 * @return boolean
	 */
	public boolean isDefaultAutoPublishing();

	/**
	 * Set whether publishing should occur automatically.
	 *
	 * @param boolean
	 */
	public void setAutoPublishing(boolean b);

	/**
	 * Returns whether servers and configurations should be created in the
	 * workspace (as opposed to in metadata).
	 *
	 * @return boolean
	 */
	public boolean isCreateResourcesInWorkspace();

	/**
	 * Returns whether servers and configurations should be created in the
	 * workspace (as opposed to in metadata) by default.
	 *
	 * @return boolean
	 */
	public boolean isDefaultCreateResourcesInWorkspace();

	/**
	 * Set whether servers and configurations should be created in the
	 * workspace (as opposed to in metadata).
	 *
	 * @param boolean
	 */
	public void setCreateResourcesInWorkspace(boolean b);
}