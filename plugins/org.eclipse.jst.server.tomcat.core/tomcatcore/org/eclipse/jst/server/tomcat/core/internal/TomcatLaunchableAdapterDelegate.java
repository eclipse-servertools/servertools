/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.net.URL;

import org.eclipse.jst.server.j2ee.IWebModule;
import org.eclipse.jst.server.j2ee.Servlet;
import org.eclipse.jst.server.j2ee.WebResource;
import org.eclipse.wst.server.core.ILaunchable;
import org.eclipse.wst.server.core.IModuleObject;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.util.HttpLaunchable;
import org.eclipse.wst.server.core.util.NullLaunchable;
import org.eclipse.wst.server.core.util.NullModuleObject;
/**
 * Launchable adapter delegate for Web resources in Tomcat.
 */
public class TomcatLaunchableAdapterDelegate extends LaunchableAdapterDelegate {
	/*
	 * @see LaunchableAdapterDelegate#getLaunchable(IServer, IModuleObject)
	 */
	public ILaunchable getLaunchable(IServer server, IModuleObject moduleObject) {
		Trace.trace("TomcatLaunchableAdapter " + server + "-" + moduleObject);
		if (server.getAdapter(TomcatServer.class) == null)
			return null;
		if (!(moduleObject instanceof Servlet) &&
			!(moduleObject instanceof WebResource) &&
			!(moduleObject instanceof NullModuleObject))
			return null;
		if (!(moduleObject.getModule() instanceof IWebModule))
			return null;

		try {
			URL url = ((IURLProvider) server.getAdapter(IURLProvider.class)).getModuleRootURL(moduleObject.getModule());
			
			Trace.trace("root: " + url);

			if (moduleObject instanceof Servlet) {
				Servlet servlet = (Servlet) moduleObject;
				if (servlet.getAlias() != null) {
					String path = servlet.getAlias();
					if (path.startsWith("/"))
						path = path.substring(1);
					url = new URL(url, path);
				} else
					url = new URL(url, "servlet/" + servlet.getServletClassName());
			} else if (moduleObject instanceof WebResource) {
				WebResource resource = (WebResource) moduleObject;
				String path = resource.getPath().toString();
				Trace.trace("path: " + path);
				if (path != null && path.startsWith("/") && path.length() > 0)
					path = path.substring(1);
				if (path != null && path.length() > 0)
					url = new URL(url, path);
			} else { // null
				return new NullLaunchable();
			}
			return new HttpLaunchable(url);
		} catch (Exception e) {
			Trace.trace("Error getting URL for " + moduleObject, e);
			return null;
		}
	}
}