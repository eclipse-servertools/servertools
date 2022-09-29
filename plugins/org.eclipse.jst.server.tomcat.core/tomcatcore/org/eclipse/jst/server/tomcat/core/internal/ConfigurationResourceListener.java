/**********************************************************************
 * Copyright (c) 2011, 2022 SAS Institute, Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    SAS Institute, Inc - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.ServerType;

public class ConfigurationResourceListener implements IResourceChangeListener {

	private IProject serversProject;
	private List<String> errorLogged = new ArrayList<>();
	
	/**
	 * Currently, only changes to Tomcat configuration files are detected and the associated
	 * server's state updated.  This method needs to be as brief as possible if the change
	 * is unrelated to server configuration changes.  Since the Servers project would change
	 * so rarely, it is worth saving some cycles in the resource listener by caching this project.
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			IProject project = getServersProject();
			if (project != null) {
				IResourceDelta delta = event.getDelta();
				if (delta != null) {
					IResourceDelta serversProjectDelta = delta.findMember(project.getFullPath());
					if (serversProjectDelta != null) {
						// The change occurred within the Servers project.
						IResourceDelta [] childDelta = serversProjectDelta.getAffectedChildren();
						if (childDelta.length > 0) {
							IServer [] servers = ServerCore.getServers();
							for (int i = 0; i < childDelta.length; i++) {
								// Check if this subfolder of the Servers folder matches a Tomcat configuration folder
								for (int j = 0; j < servers.length; j++) {
									IServerType serverType = servers[j].getServerType();
									String tomcatServerTypePrefix = "org.eclipse.jst.server.tomcat.";
									// potential NPE arises if the runtime is renamed
									if (serverType == null) {
										if (!errorLogged.contains(servers[j].getName())) {
											errorLogged.add(servers[j].getName());
											TomcatPlugin.log("Could not determine server type for " + servers[j].getName());
										}
									}
									else if (serverType.getId() != null && serverType.getId().length() > tomcatServerTypePrefix.length() && tomcatServerTypePrefix.equals(serverType.getId().substring(0, tomcatServerTypePrefix.length()))) {
										IFolder configFolder = servers[j].getServerConfiguration();
										if (configFolder != null) {
											if (childDelta[i].getFullPath().equals(configFolder.getFullPath())) {
												// Found a Tomcat server affected by this delta.  Update this server's publish state.
												TomcatServerBehaviour tcServerBehaviour = (TomcatServerBehaviour)servers[j].loadAdapter(TomcatServerBehaviour.class, null);
												if (tcServerBehaviour != null) {
													// Indicate that this server needs to publish and restart if running
													tcServerBehaviour.setTomcatServerPublishState(IServer.PUBLISH_STATE_INCREMENTAL);
													tcServerBehaviour.setTomcatServerRestartState(true);
												}
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private IProject getServersProject() {
		if (serversProject == null) {
			IProject project;
			try {
				project = ServerType.getServerProject();
				synchronized (this) {
					serversProject = project;
				}
			} catch (CoreException e) {
				// Ignore
			}
		}
		return serversProject;
	}
}
