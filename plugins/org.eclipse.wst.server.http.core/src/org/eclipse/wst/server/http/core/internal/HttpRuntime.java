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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * 
 */
public class HttpRuntime extends RuntimeDelegate {
	public static final String ID = "org.eclipse.wst.server.http.runtime";

	public HttpRuntime() {
		// do nothing
	}

	public IStatus validate() {
		return Status.OK_STATUS;
	}

	/*
	 * @see RuntimeDelegate#setDefaults(IProgressMonitor)
	 */
	public void setDefaults(IProgressMonitor monitor) {
		getRuntimeWorkingCopy().setLocation(new Path(""));
	}
}