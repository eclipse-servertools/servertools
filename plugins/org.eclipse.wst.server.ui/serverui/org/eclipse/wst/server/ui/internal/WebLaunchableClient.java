/*******************************************************************************
 * Copyright (c) 2003, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ClientDelegate;
import org.eclipse.wst.server.core.util.HttpLaunchable;
/**
 *
 */
public class WebLaunchableClient extends ClientDelegate {
	/*
	 * @see ClientDelegate#supports(ILaunchable)
	 */
	public boolean supports(IServer server, Object launchable, String launchMode) {
		return (launchable instanceof HttpLaunchable);
	}

	/*
	 * @see ClientDelegate#launch(ILaunchable)
	 */
	public IStatus launch(IServer server, Object launchable, String launchMode, ILaunch launch) {
		HttpLaunchable http = (HttpLaunchable) launchable;
		try {
			IWorkbenchBrowserSupport browserSupport = ServerUIPlugin.getInstance().getWorkbench().getBrowserSupport();
			IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, null, null);
			browser.openURL(http.getURL());
			return Status.OK_STATUS;
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error opening browser", e);
			}
			return new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, e.getMessage(), e);
		}
	}
}