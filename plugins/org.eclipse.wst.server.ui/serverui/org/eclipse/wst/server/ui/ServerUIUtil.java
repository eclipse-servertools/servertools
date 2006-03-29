/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui;

import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

import org.eclipse.swt.widgets.Shell;
/**
 * Server UI utility methods.
 * 
 * @since 1.0
 */
public class ServerUIUtil {
	/**
	 * ServerUIUtil constructor comment.
	 */
	private ServerUIUtil() {
		super();
	}

	/**
	 * Open the new runtime wizard.
	 * 
	 * @param shell a shell to use when creating the wizard
	 * @param type the type of module to create a runtime for
	 * @param version the version of module to create a runtime for
	 * @return <code>true</code> if a runtime was created, or
	 *    <code>false</code> otherwise
	 */
	public static boolean showNewRuntimeWizard(Shell shell, final String type, final String version) {
		return ServerUIPlugin.showNewRuntimeWizard(shell, type, version, null);
	}
}