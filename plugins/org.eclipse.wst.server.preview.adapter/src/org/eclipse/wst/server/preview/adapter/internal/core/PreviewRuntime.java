/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.adapter.internal.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.osgi.framework.Bundle;
/**
 * HTTP preview runtime.
 */
public class PreviewRuntime extends RuntimeDelegate {
	public static final String ID = "org.eclipse.wst.server.preview.runtime";

	/**
	 * Create a new preview runtime.
	 */
	public PreviewRuntime() {
		// do nothing
	}

	/**
	 * Returns the path that corresponds to the specified bundle.
	 * 
	 * @return a path
	 */
	protected static Path getPluginPath(Bundle bundle) {
		try {
			URL installURL = bundle.getEntry("/");
			URL localURL = FileLocator.toFileURL(installURL);
			return new Path(localURL.getFile());
		} catch (IOException ioe) {
			return null;
		}
	}

	protected static IPath getJarredPluginPath(Bundle bundle) {
		try {
			File file = FileLocator.getBundleFile(bundle);
			return new Path(file.getCanonicalPath());
		} catch (IOException e) {
			// ignore, return null
			return null;
		}
	}

	public IStatus validate() {
		IStatus status = super.validate();
		if (!status.isOK() && status.getMessage().length() > 0)
			return status;
		
		return Status.OK_STATUS;
	}
}