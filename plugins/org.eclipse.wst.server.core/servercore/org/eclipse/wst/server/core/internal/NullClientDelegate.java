/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ClientDelegate;
/**
 * A client delegate that does nothing. Application will be launched
 * for the user, but no client application will open.
 */
public class NullClientDelegate extends ClientDelegate {
	public boolean supports(IServer server, Object launchable, String launchMode) {
		return launchable instanceof NullLaunchableAdapterDelegate.NullLaunchable;
	}

	public IStatus launch(IServer server, Object launchable, String launchMode, ILaunch launch) {
		return Status.OK_STATUS;
	}
}