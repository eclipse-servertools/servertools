/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.ILaunchable;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ClientDelegate;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
/**
 * 
 */
public class NullLaunchableClient extends ClientDelegate {
	/*
	 * @see ILaunchableClient#supports(ILaunchable)
	 */
	public boolean supports(IServer server, ILaunchable launchable, String launchMode) {
		return (launchable instanceof NullLaunchable);
	}
	
	public IStatus launch(IServer server, ILaunchable launchable, String launchMode, ILaunch launch) {
		return null;
	}
}