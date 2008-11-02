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

import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.discovery.internal.ExtensionUtility;

public class Discovery {
	public static boolean launchExtensionWizard(Shell shell, String title, String message) {
		return ExtensionUtility.launchExtensionWizard(shell, title, message);
	}
}