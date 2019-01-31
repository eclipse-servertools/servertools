/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServerAttributes;
import org.eclipse.wst.server.ui.RemoveModuleMessageExtension;

public class RemoveModuleMessageTestExtension extends RemoveModuleMessageExtension {
	
	public static String customRemoveMessage = "Are you sure you want to remove one or more modules from the server?";
	
	public RemoveModuleMessageTestExtension() {
		// Empty
	}

	@Override
	public String getConfirmationMessage(IServerAttributes server, IModule[] modules) {
		return customRemoveMessage;
	}

}