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

public interface IHttpRuntimeWorkingCopy {
	public void setPublishLocation(String location);

	public void setPort(int port);

	public void setPrefixPath(String prefixPath);

	public String getPublishLocation();

	public int getPort();

	public String getPrefixPath();

	public boolean publishToDirectory();

	public void setPublishToDirectory(boolean canPublish);
}