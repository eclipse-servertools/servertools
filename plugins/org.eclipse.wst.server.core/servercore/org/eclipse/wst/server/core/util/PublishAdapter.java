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

import org.eclipse.wst.server.core.IPublishStatus;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IPublishListener;
import org.eclipse.wst.server.core.resources.IModuleResource;
import org.eclipse.wst.server.core.resources.IRemoteResource;
/**
 *
 */
public class PublishAdapter implements IPublishListener {
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleStateChange(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule)
	 */
	public void moduleStateChange(IServer server, List parents, IModule module) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#publishStarting(org.eclipse.wst.server.core.IServer2, java.util.List[], org.eclipse.wst.server.core.model.IModule[])
	 */
	public void publishStarting(IServer server, List[] parents, IModule[] module) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#publishStarted(org.eclipse.wst.server.core.IServer2, org.eclipse.wst.server.core.IPublishStatus)
	 */
	public void publishStarted(IServer server, IPublishStatus status) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleStarting(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule)
	 */
	public void moduleStarting(IServer server, List parents, IModule module) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleResourcesPublished(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule, org.eclipse.wst.server.core.resources.IModuleResource[], org.eclipse.wst.server.core.IPublishStatus[])
	 */
	public void moduleResourcesPublished(IServer server, List parents, IModule module, IModuleResource[] published, IPublishStatus[] status) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleResourcesDeleted(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule, org.eclipse.wst.server.core.resources.IRemoteResource[], org.eclipse.wst.server.core.IPublishStatus[])
	 */
	public void moduleResourcesDeleted(IServer server, List parents, IModule module, IRemoteResource[] deleted, IPublishStatus[] status) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleFinished(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule, org.eclipse.wst.server.core.IPublishStatus)
	 */
	public void moduleFinished(IServer server, List parents, IModule module, IPublishStatus status) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#publishFinished(org.eclipse.wst.server.core.IServer, org.eclipse.wst.server.core.IPublishStatus)
	 */
	public void publishFinished(IServer server, IPublishStatus status) {
	}
}
