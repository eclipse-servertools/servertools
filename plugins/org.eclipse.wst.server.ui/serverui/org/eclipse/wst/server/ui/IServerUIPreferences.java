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
package org.eclipse.wst.server.ui;
/**
 * Helper class that stores preference information for
 * the server tools UI.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerUIPreferences {
	public static final byte SAVE_EDITORS_NEVER = 0;
	public static final byte SAVE_EDITORS_PROMPT = 1;
	public static final byte SAVE_EDITORS_AUTO = 2;

	/**
	 * Returns whether the publishing details should be shown.
	 *
	 * @return boolean
	 */
	public boolean getShowPublishingDetails();

	/**
	 * Returns whether the publishing details should be shown by default.
	 *
	 * @return boolean
	 */
	public boolean getDefaultShowPublishingDetails();

	/**
	 * Sets whether the publishing details should be shown.
	 *
	 * @return boolean
	 */
	public void setShowPublishingDetails(boolean b);

	/**
	 * Returns whether the user should be prompted before making an
	 * irreversible change in the editor.
	 * 
	 * @return boolean
	 */
	public boolean getPromptBeforeIrreversibleChange();

	/**
	 * Returns the default value of whether the user should be prompted
	 * before making an irreversible change in the editor.
	 *
	 * @return boolean
	 */
	public boolean getDefaultPromptBeforeIrreversibleChange();

	/**
	 * Sets whether the user should be prompted before making an
	 * irreversible change in the editor.
	 *
	 * @return boolean
	 */
	public void setPromptBeforeIrreversibleChange(boolean b);
	
	/**
	 * Returns the default setting for saving editors before launching.
	 * 
	 * @return byte
	 */
	public byte getDefaultSaveEditors();

	/**
	 * Returns the setting for saving editors before launching.
	 * 
	 * @return byte
	 */
	public byte getSaveEditors();

	/**
	 * Sets the value for saving editors before launching.
	 * 
	 * @param byte
	 */
	public void setSaveEditors(byte b);
}
