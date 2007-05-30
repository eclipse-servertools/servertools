/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.actions;

import org.eclipse.debug.core.ILaunchManager;
/**
 * 
 */
public class ProfileOnServerActionDelegate extends RunOnServerActionDelegate {
	/**
	 * Returns the start mode that the server should use.
	 */
	protected String getLaunchMode() {
		return ILaunchManager.PROFILE_MODE;
	}
}