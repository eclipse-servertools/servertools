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
package org.eclipse.wst.server.http.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * 
 */
public class HttpRuntime extends RuntimeDelegate implements IHttpRuntimeWorkingCopy {
	public static final String PROP_PUBLISH_LOCATION = "publishLocation";
	public static final String PROP_PORT = "serverPort";
	public static final String PROP_PREFIX = "prefixPath";
	public static final String PROP_CAN_PUBLISH = "canPublish";
	// public static final String PROP_BASE_URL = "baseURL";

	public static final String ID = "org.eclipse.wst.server.http.runtime";

	public HttpRuntime() {
		// do nothing
	}

	public IStatus validate() {
		return Status.OK_STATUS;
	}

	/**
	 * @see RuntimeDelegate#setDefaults(IProgressMonitor)
	 */
	public void setDefaults(IProgressMonitor monitor) {
		setPrefixPath("");
		setPublishLocation("");
		setPublishToDirectory(true);
		setPort(80);
	}

	public String getPublishLocation() {
		return getAttribute(PROP_PUBLISH_LOCATION, (String) null);
	}

	public void setPublishLocation(String location) {
		setAttribute(PROP_PUBLISH_LOCATION, location);
	}

	public int getPort() {
		return getAttribute(PROP_PORT, -1);
	}

	public String getPrefixPath() {
		return getAttribute(PROP_PREFIX, (String) null);
	}

	public void setPort(int port) {
		setAttribute(PROP_PORT, port);
	}

	public void setPrefixPath(String prefixPath) {
		setAttribute(PROP_PREFIX, prefixPath);
	}

	public boolean publishToDirectory() {
		return getAttribute(PROP_CAN_PUBLISH, false);
	}

	public void setPublishToDirectory(boolean canPublish) {
		setAttribute(PROP_CAN_PUBLISH, canPublish);
	}
}