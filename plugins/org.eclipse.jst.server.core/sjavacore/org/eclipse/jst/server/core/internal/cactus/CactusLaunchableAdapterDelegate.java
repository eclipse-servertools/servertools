/*******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel R. Somerfield - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal.cactus;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.server.core.internal.Trace;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IURLProvider;
import org.eclipse.wst.server.core.model.LaunchableAdapterDelegate;
/**
 *
 */
public class CactusLaunchableAdapterDelegate extends LaunchableAdapterDelegate {
	public Object getLaunchable(IServer server, IModuleArtifact moduleArtifact) throws CoreException {
		if (moduleArtifact instanceof WebTestableResource) {
			WebTestableResource resource = (WebTestableResource) moduleArtifact;
			URL url = ((IURLProvider) server.getAdapter(IURLProvider.class))
					.getModuleRootURL(resource.getModule());
			
			String urlString = url.toString();
			if (urlString.endsWith("/")) {
				try {
					url = new URL(urlString.substring(0, urlString.length() - 1));
				} catch (MalformedURLException e) {
					Trace.trace(Trace.SEVERE, "Error getting launchable", e);
					return null;
				}
			}
			return new CactusLaunchable(resource.getProjectName(),
					resource.getClassName(), resource.getTestName(), url);
		}
		return null;
	}
}