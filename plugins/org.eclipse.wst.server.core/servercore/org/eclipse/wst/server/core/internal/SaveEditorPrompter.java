/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.server.core.internal;

/**
 * <p>
 * SaveEditorPrompter is the abstract implementation of the .saveEditorPrompter extension 
 * point. This class is used for prompting to the user to save all the editors. The class 
 * transfer the control from the non-ui code to the UI code via an the extension point
 * </p>
 * <b>This class is not intended to be extended adopters.</b> 
 * 
 */
public class SaveEditorPrompter {
	
	/**
	 * Move the control to the UI and save all the editors according to <code>org.eclipse.debug.internal.ui.IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH</code>  
	 */
	public void saveAllEditors(){
		// nothing to do, should be implemented		
	}
	public void setDebugNeverSave(){
		// nothing to do, should be implemented		
	}
	public void setDebugOriginalValue(){
		// nothing to do, should be implemented		
	}
}
