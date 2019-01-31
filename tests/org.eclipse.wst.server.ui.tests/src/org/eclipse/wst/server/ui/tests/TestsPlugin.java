/*******************************************************************************
 * Copyright (c) 2004, 2015 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.tests;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
/**
 * 
 */
public class TestsPlugin extends Plugin {
	
	private static TestsPlugin plugin;
	public static final String PLUGIN_ID = "org.eclipse.wst.server.ui.tests"; //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public TestsPlugin() {
		super();
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}
	
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}	
	
	public static TestsPlugin getDefault() {
		return plugin;
	}
}