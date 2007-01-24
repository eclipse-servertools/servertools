/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jst.server.core.JndiLaunchable;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ClientDelegate;
/**
 *
 */
public class J2EELaunchableClient extends ClientDelegate {
	/*
	 * @see ClientDelegate#supports(ILaunchable)
	 */
	public boolean supports(IServer server, Object launchable, String launchMode) {
		return (launchable instanceof JndiLaunchable);
	}

	/*
	 * @see ClientDelegate#launch(ILaunchable)
	 */
	public IStatus launch(IServer server, Object launchable, String launchMode, ILaunch launch) {
		Trace.trace(Trace.FINEST, "JNDI client launched");
		return null;
	}
}