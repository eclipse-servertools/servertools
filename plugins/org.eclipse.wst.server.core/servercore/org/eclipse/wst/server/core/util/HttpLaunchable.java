/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import java.net.URL;

import org.eclipse.wst.server.core.ILaunchable;
/**
 *
 */
public class HttpLaunchable implements ILaunchable {
	public static final String ID = "http.launchable";

	private URL url;

	public HttpLaunchable(URL url) {
		this.url = url;
	}

	public String getId() {
		return ID;
	}

	public URL getURL() {
		return url;
	}

	public String toString() {
		return "HttpLaunchable[id=" + getId() + ", url=" + url.toString() + "]";
	}
}