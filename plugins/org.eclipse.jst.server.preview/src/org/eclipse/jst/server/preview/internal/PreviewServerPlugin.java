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
package org.eclipse.jst.server.preview.internal;

import org.eclipse.core.runtime.*;
import org.osgi.framework.BundleContext;
/**
 * 
 */
public class PreviewServerPlugin extends Plugin {
	public static final String PLUGIN_ID = "org.eclipse.jst.server.preview.core";

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

	/* D:\jres\jdk1.5.0_01\bin\javaw.exe -Xms40m -Xmx256m 
	 * -classpath D:\dev\wtp\eclipse\plugins\org.eclipse.equinox.launcher_1.0.0.v20070208a.jar
	 *  org.eclipse.equinox.launcher.Main -launcher D:\dev\wtp\eclipse\eclipse -name Eclipse
	 *   -showsplash 600 -product org.eclipse.sdk.ide -data D:\dev\wtp\runtime-workspace5
	 *    -configuration file:D:/dev/wtp/workspace/.metadata/.plugins/org.eclipse.pde.core/New_configuration/
	 *     -dev file:D:/dev/wtp/workspace/.metadata/.plugins/org.eclipse.pde.core/New_configuration/dev.properties
	 *      -pdelaunch -debug D:\dev\wtp\workspace\.metadata\.plugins\org.eclipse.pde.core\New_configuration/.options
	 *       -os win32 -ws win32 -arch x86 -consolelog*/
}