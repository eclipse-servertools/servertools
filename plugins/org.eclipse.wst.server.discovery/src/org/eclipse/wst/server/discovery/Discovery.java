/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class Discovery {
	public static boolean launchExtensionWizard(Shell shell, String title, String message) {
		ExtensionWizard wizard = new ExtensionWizard();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		if (dialog.open() != Window.CANCEL)
			return true;
		return false;
	}
}