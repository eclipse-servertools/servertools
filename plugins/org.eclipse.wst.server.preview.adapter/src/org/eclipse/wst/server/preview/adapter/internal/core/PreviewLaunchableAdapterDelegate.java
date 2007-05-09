/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.adapter.internal.core;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.LaunchableAdapterDelegate;
import org.eclipse.wst.server.core.util.HttpLaunchable;
import org.eclipse.wst.server.core.util.WebResource;

public class PreviewLaunchableAdapterDelegate extends LaunchableAdapterDelegate {
	/*
	 * @see LaunchableAdapterDelegate#getLaunchable(IServer, IModuleArtifact)
	 */
	public Object getLaunchable(IServer server, IModuleArtifact moduleArtifact) throws CoreException {
		if (server == null || moduleArtifact == null)
			return null;
		
		PreviewServer server2 = (PreviewServer) server.loadAdapter(PreviewServer.class, null);
		if (server2 == null)
			return null;
		
		try {
			URL url = server2.getModuleRootURL(moduleArtifact.getModule());
			
			if (moduleArtifact instanceof WebResource) {
				WebResource resource = (WebResource) moduleArtifact;
				String path = resource.getPath().toString();
				
				if (path.startsWith("/"))
					path = path.substring(1);
				url = new URL(url.toExternalForm() + "/" + path);
			}
			return new HttpLaunchable(url);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error in launchable adapter", e);
		}
		
		return null;
	}
}