/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.core.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IServer;
/**
 * Helper class which implements the IPublishListener interface
 * with empty methods.
 * 
 * @see org.eclipse.wst.server.core.IPublishListener
 * @since 1.0
 */
public class PublishAdapter implements IPublishListener {
	/**
	 * @see IPublishListener#publishStarted(IServer)
	 */
	public void publishStarted(IServer server) {
		// do nothing
	}

	/**
	 * @see IPublishListener#publishFinished(IServer, IStatus)
	 */
	public void publishFinished(IServer server, IStatus status) {
		// do nothing
	}
}