/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IServer;
/**
 *
 */
public class PublishAdapter implements IPublishListener {
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#publishStarted(org.eclipse.wst.server.core.IServer2)
	 */
	public void publishStarted(IServer server) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleStarting(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule)
	 */
	public void publishModuleStarted(IServer server, IModule[] parents, IModule module) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleFinished(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule, org.eclipse.wst.server.core.IPublishStatus)
	 */
	public void publishModuleFinished(IServer server, IModule[] parents, IModule module, IStatus status) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#publishFinished(org.eclipse.wst.server.core.IServer, org.eclipse.wst.server.core.IPublishStatus)
	 */
	public void publishFinished(IServer server, IStatus status) {
		// do nothing
	}
}