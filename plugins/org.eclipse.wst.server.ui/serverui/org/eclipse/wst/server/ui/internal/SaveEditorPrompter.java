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
package org.eclipse.wst.server.ui.internal;

import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class SaveEditorPrompter extends
		org.eclipse.wst.server.core.internal.SaveEditorPrompter { 
	
	private String cachedSaveBeforeLaunch;
	
	@Override
	public void saveAllEditors() {
		IWorkbench w = PlatformUI.getWorkbench();
		String saveBeforeLaunch = DebugUITools.getPreferenceStore().getString(org.eclipse.debug.internal.ui.IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH);
		if (saveBeforeLaunch.equalsIgnoreCase(org.eclipse.jface.dialogs.MessageDialogWithToggle.ALWAYS)){
			Display d =PlatformUI.getWorkbench().getDisplay();
			d.asyncExec(new SaveAllEditorsRunnable(w,false));
		}
		else if (saveBeforeLaunch.equalsIgnoreCase(org.eclipse.jface.dialogs.MessageDialogWithToggle.PROMPT)){
			Display d =PlatformUI.getWorkbench().getDisplay();
			d.asyncExec(new SaveAllEditorsRunnable(w,true));
		}
	}
	
	private class SaveAllEditorsRunnable implements Runnable{
		IWorkbench w;
		boolean confirm;
		public SaveAllEditorsRunnable(IWorkbench w, boolean confirm){
			this.w = w;
			this.confirm = confirm;
		}
		
		public void run() {
			w.saveAllEditors(confirm);
		}
	}
		
	/**
	 * Sets <code>org.eclipse.debug.internal.ui.IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH</code> to 
	 * the given value. The given value should be one of the following:
	 * <ul>
	 * <li><code>org.eclipse.jface.dialogs.MessageDialogWithToggle.ALWAYS</code></li>
	 * <li><code>org.eclipse.jface.dialogs.MessageDialogWithToggle.NEVER</code></li>
	 * <li><code>org.eclipse.jface.dialogs.MessageDialogWithToggle.PROMPT</code></li>
	 * </ul>  
	 * @see org.eclipse.jface.dialogs.MessageDialogWithToggle
	 */
	public String setDebugSaveBeforeLaunching(String newValue){
		IPreferenceStore debugPrefs = DebugUITools.getPreferenceStore();
		String oldValue = debugPrefs.getString(org.eclipse.debug.internal.ui.IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH);
		debugPrefs.setValue(org.eclipse.debug.internal.ui.IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH, newValue);
		return oldValue; 
	}
	
	public void setDebugNeverSave(){
		cachedSaveBeforeLaunch = setDebugSaveBeforeLaunching(org.eclipse.jface.dialogs.MessageDialogWithToggle.NEVER);
	}
	
	public void setDebugOriginalValue(){
		if (cachedSaveBeforeLaunch == null){
			cachedSaveBeforeLaunch = org.eclipse.jface.dialogs.MessageDialogWithToggle.PROMPT;
		}
		setDebugSaveBeforeLaunching(cachedSaveBeforeLaunch);
	}
}
