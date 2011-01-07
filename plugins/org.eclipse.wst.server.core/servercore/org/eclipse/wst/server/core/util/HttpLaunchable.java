/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.util;

import java.net.URL;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IURLProvider2;
/**
 * An HTTP launchable object. Encapsulates a launch of a URL for the Run on Server
 * support.
 * 
 * @since 1.0
 */
public class HttpLaunchable {
	private IURLProvider2 urlProvider;

	/**
	 * Create a reference to something accessible via HTTP.
	 * 
	 * @param url the URL to the object
	 */
	public HttpLaunchable(final URL url) {
		this.urlProvider = new IURLProvider2() {
			public URL getModuleRootURL(IModule module){
				return url;
			}
			public URL getLaunchableURL() {
				return getModuleRootURL(null);
			}
		};
	}

	public HttpLaunchable(IURLProvider2 urlProvider){
		this.urlProvider = urlProvider;
	}
	/**
	 * Return the URL to the object.
	 * 
	 * @return the URL to the object
	 */
	public URL getURL() {
		return urlProvider.getLaunchableURL();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "HttpLaunchable[urlProvider=" + urlProvider + "]";
	}
}