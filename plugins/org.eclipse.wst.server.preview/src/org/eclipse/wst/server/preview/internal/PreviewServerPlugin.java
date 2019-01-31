/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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
package org.eclipse.wst.server.preview.internal;

import org.eclipse.core.runtime.*;
import org.osgi.framework.BundleContext;
/**
 * 
 */
public class PreviewServerPlugin extends Plugin {
	public static final String PLUGIN_ID = "org.eclipse.wst.server.preview";

	// singleton instance of this class
	private static PreviewServerPlugin singleton;

	protected BundleContext context;

	/**
	 * Create the JavaServerPlugin.
	 */
	public PreviewServerPlugin() {
		super();
		singleton = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 *
	 * @return a singleton instance
	 */
	public static PreviewServerPlugin getInstance() {
		return singleton;
	}

	/**
	 * @see Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context2) throws Exception {
		super.start(context2);
		context = context2;
	}

	/**
	 * @see Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context2) throws Exception {
		super.stop(context2);
	}
}