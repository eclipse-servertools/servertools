/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
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
	public static final byte REPAIR_NEVER = 0;
	public static final byte REPAIR_PROMPT = 1;
	public static final byte REPAIR_ALWAYS = 2;

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
	 * Returns whether automatic publishing should occur before
	 * starting a server.
	 *
	 * @return boolean
	 */
	public boolean isAutoPublishing();
	
	/**
	 * Returns whether automatic publishing should occur before
	 * starting a server.
	 *
	 * @return boolean
	 */
	public boolean isDefaultAutoPublishing();

	/**
	 * Set whether publishing should happen before the server starts.
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

	/**
	 * Returns whether changes to modules should be automatically fixed
	 * in the server configurations. Returns one of the REPAIR_* constants.
	 *
	 * @return byte
	 */
	public byte getModuleRepairStatus();

	/**
	 * Returns the default module fix state. Returns one of the
	 * REPAIR_* constants.
	 *
	 * @return byte
	 */
	public byte getDefaultModuleRepairStatus();

	/**
	 * Sets whether changes to modules should be automatically fixed
	 * in the server configurations. Use one of the REPAIR_* constants.
	 *
	 * @return byte
	 */
	public void setModuleRepairStatus(byte b);
}