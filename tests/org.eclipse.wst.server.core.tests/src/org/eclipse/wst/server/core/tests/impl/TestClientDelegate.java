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
package org.eclipse.wst.server.core.tests.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.wst.server.core.ILaunchable;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ClientDelegate;

public class TestClientDelegate extends ClientDelegate {
	public boolean supports(IServer server, ILaunchable launchable, String launchMode) {
		return false;
	}

	public IStatus launch(IServer server, ILaunchable launchable, String launchMode, ILaunch launch) {
		return null;
	}
}