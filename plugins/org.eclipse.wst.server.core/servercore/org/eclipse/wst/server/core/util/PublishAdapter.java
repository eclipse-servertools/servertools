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

import java.util.List;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IPublishStatus;
import org.eclipse.wst.server.core.IServer;
/**
 *
 */
public class PublishAdapter implements IPublishListener {
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleStateChange(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule)
	 */
	public void moduleStateChange(IServer server, IModule[] parents, IModule module) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#publishStarting(org.eclipse.wst.server.core.IServer2, java.util.List[], org.eclipse.wst.server.core.model.IModule[])
	 */
	public void publishStarting(IServer server, List[] parents, IModule[] module) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#publishStarted(org.eclipse.wst.server.core.IServer2)
	 */
	public void publishStarted(IServer server) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleStarting(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule)
	 */
	public void moduleStarting(IServer server, IModule[] parents, IModule module) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleFinished(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule, org.eclipse.wst.server.core.IPublishStatus)
	 */
	public void moduleFinished(IServer server, IModule[] parents, IModule module, IPublishStatus status) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#publishFinished(org.eclipse.wst.server.core.IServer, org.eclipse.wst.server.core.IPublishStatus)
	 */
	public void publishFinished(IServer server, IPublishStatus status) {
		// do nothing
	}
}