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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
/**
 * Helper class which implements the IPublishListener interface
 * with empty methods.
 * 
 * @see org.eclipse.wst.server.core.internal.IPublishListener
 * @since 1.0
 */
public class PublishAdapter implements IPublishListener {
	public void publishStarted(IServer server) {
		// do nothing
	}

	public void publishModuleStarted(IServer server, IModule[] parents, IModule module) {
		// do nothing
	}

	public void publishModuleFinished(IServer server, IModule[] parents, IModule module, IStatus status) {
		// do nothing
	}

	public void publishFinished(IServer server, IStatus status) {
		// do nothing
	}
}