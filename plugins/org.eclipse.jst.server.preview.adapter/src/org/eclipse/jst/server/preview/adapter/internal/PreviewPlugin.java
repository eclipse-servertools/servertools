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
package org.eclipse.jst.server.preview.adapter.internal;

import org.eclipse.core.runtime.*;
/**
 * The main preview server tools plugin class.
 */
public class PreviewPlugin extends Plugin {
	/**
	 * Java server plugin id
	 */
	public static final String PLUGIN_ID = "org.eclipse.jst.server.preview.adapter";

	// singleton instance of this class
	private static PreviewPlugin singleton;

	/**
	 * Create the PreviewPlugin.
	 */
	public PreviewPlugin() {
		super();
		singleton = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 *
	 * @return a singleton instance
	 */
	public static PreviewPlugin getInstance() {
		return singleton;
	}

	/**
	 * Convenience method for logging.
	 *
	 * @param status a status
	 */
	private static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	public static void logWarning(String msg) {
		log(new Status(IStatus.WARNING, PLUGIN_ID, IStatus.OK, msg, null));
	}
}