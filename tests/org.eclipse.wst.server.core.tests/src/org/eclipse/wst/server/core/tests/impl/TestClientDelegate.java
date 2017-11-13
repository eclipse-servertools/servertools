/*******************************************************************************
 * Copyright (c) 2005,2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Karsten Thoms <karsten.thoms@itemis.de> - Bug#527229
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ClientDelegate;

public class TestClientDelegate extends ClientDelegate {
	public boolean supports(IServer server, Object launchable, String launchMode) {
		return false;
	}

	public IStatus launch(IServer server, Object launchable, String launchMode, ILaunch launch) {
		return Status.OK_STATUS;
	}
}