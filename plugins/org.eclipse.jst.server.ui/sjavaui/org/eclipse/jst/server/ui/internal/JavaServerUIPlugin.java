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
package org.eclipse.jst.server.ui.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
/**
 * The main server tooling plugin class.
 */
public class JavaServerUIPlugin extends AbstractUIPlugin {
	/**
	 * Java server UI plugin id
	 */
	public static final String PLUGIN_ID = "org.eclipse.jst.server.ui";

	// singleton instance of this class
	private static JavaServerUIPlugin singleton;

	/**
	 * Create the JavaServerUIPlugin.
	 */
	public JavaServerUIPlugin() {
		super();
		singleton = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 *
	 * @return org.eclipse.jst.server.ui.JavaServerUIPlugin
	 */
	public static JavaServerUIPlugin getInstance() {
		return singleton;
	}
}