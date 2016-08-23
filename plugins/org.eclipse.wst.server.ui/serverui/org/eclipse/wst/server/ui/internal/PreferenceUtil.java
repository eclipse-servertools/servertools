/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServerAttributes;
import org.eclipse.wst.server.ui.RemoveModuleMessageExtension;

public class PreferenceUtil {
	
	public static boolean confirmModuleRemoval(IServerAttributes server, Shell shell, List<IModule> moduleList) {
		// Get preference value to see if the confirmation dialog should appear or not. Default is to show the dialog.
		boolean doNotShowDialog = ServerUIPlugin.getPreferences().getDoNotShowRemoveModuleWarning();
		if (!doNotShowDialog) {
			String message = null;

			String id = server.getServerType().getId();
			RemoveModuleMessageExtension targetExtension = ServerUIPlugin.getRemoveModuleMessageExtension(id);
			String customMessage = null;
			
			if (moduleList.size() > 0) {
				if (targetExtension != null) {
					customMessage = targetExtension.getConfirmationMessage(server, moduleList.toArray(new IModule [0]));
				}
				// The extension might still return null or an empty string.  If so, then use the default/original message.
				if (customMessage == null || customMessage.length() == 0) {
					if (moduleList.size() == 1) {
						message = Messages.dialogRemoveModuleConfirm; // Default singular
					} else {
						message = Messages.dialogRemoveModulesConfirm; // Default plural
					}
				} else {
					message = customMessage;
				}
			}	
		
			MessageDialogWithToggle messageWithToggle = MessageDialogWithToggle.openOkCancelConfirm(shell, Messages.defaultDialogTitle, 
				message, Messages.doNotShowAgain, false, null, null);
			// If cancel is pressed, do not update the preference even if the checkbox changed.
			if (messageWithToggle.getReturnCode() == Window.CANCEL) {
				// Do not remove, return false
				return false;
			}
			// If OK was pressed, then also update the preference
			ServerUIPlugin.getPreferences().setDoNotShowRemoveModuleWarning(messageWithToggle.getToggleState());
		}
		// Proceed with removal
		return true;
	}
}
