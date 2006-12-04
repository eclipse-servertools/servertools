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
 * <p>
 * This class provides all its functionality through static members.
 * It is not intended to be subclassed or instantiated.
 * </p>
 * @since 1.0
 */
public final class ServerUIUtil {
	/**
	 * Cannot instantiate ServerUIUtil - use static methods.
	 */
	private ServerUIUtil() {
		// can't create
	}

	/**
	 * Open the new runtime wizard. The given typeId and versionId are used to filter
	 * the set of runtimes displayed.
	 * 
	 * @param shell a shell to use when creating the wizard
	 * @param typeId a module type id, or null for any module type
	 * @param versionId a module version, or null for any version
	 * @return <code>true</code> if a runtime was created, or
	 *    <code>false</code> otherwise
	 */
	public static boolean showNewRuntimeWizard(Shell shell, String typeId, String versionId) {
		return ServerUIPlugin.showNewRuntimeWizard(shell, typeId, versionId, null);
	}
}