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
package org.eclipse.wst.server.core.util;

import java.net.URL;
/**
 *
 * @since 1.0
 */
public class HttpLaunchable {
	private URL url;

	/**
	 * Create a reference to something accessible via HTTP.
	 * 
	 * @param url the URL to the object
	 */
	public HttpLaunchable(URL url) {
		this.url = url;
	}

	/**
	 * Return the URL to the object.
	 * 
	 * @return the URL to the object
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "HttpLaunchable[url=" + url.toString() + "]";
	}
}