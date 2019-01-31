/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
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
package org.eclipse.jst.server.tomcat.ui.internal;
/**
 * Constant ids for context help.
 */
public interface ContextIds {
	public static final String SERVER_EDITOR = TomcatUIPlugin.PLUGIN_ID + ".teig0000";
	public static final String SERVER_EDITOR_TEST_ENVIRONMENT = TomcatUIPlugin.PLUGIN_ID + ".teig0002";
	public static final String SERVER_EDITOR_SECURE = TomcatUIPlugin.PLUGIN_ID + ".teig0004";
	public static final String SERVER_EDITOR_DEBUG_MODE = TomcatUIPlugin.PLUGIN_ID + ".teig0006";

	public static final String CONFIGURATION_EDITOR_WEBMODULES = TomcatUIPlugin.PLUGIN_ID + ".tecw0000";
	public static final String CONFIGURATION_EDITOR_WEBMODULES_LIST = TomcatUIPlugin.PLUGIN_ID + ".tecw0002";
	public static final String CONFIGURATION_EDITOR_WEBMODULES_ADD_PROJECT = TomcatUIPlugin.PLUGIN_ID + ".tecw0004";
	public static final String CONFIGURATION_EDITOR_WEBMODULES_ADD_EXTERNAL = TomcatUIPlugin.PLUGIN_ID + ".tecw0006";
	public static final String CONFIGURATION_EDITOR_WEBMODULES_EDIT = TomcatUIPlugin.PLUGIN_ID + ".tecw0008";
	public static final String CONFIGURATION_EDITOR_WEBMODULES_REMOVE = TomcatUIPlugin.PLUGIN_ID + ".tecw0010";

	public static final String CONFIGURATION_EDITOR_WEBMODULE_DIALOG = TomcatUIPlugin.PLUGIN_ID + ".tdwm0000";
	public static final String CONFIGURATION_EDITOR_WEBMODULE_DIALOG_PROJECT = TomcatUIPlugin.PLUGIN_ID + ".tdpr0002";
	public static final String CONFIGURATION_EDITOR_WEBMODULE_DIALOG_PATH = TomcatUIPlugin.PLUGIN_ID + ".tdpr0004";
	public static final String CONFIGURATION_EDITOR_WEBMODULE_DIALOG_DOCBASE = TomcatUIPlugin.PLUGIN_ID + ".tdpr0006";
	public static final String CONFIGURATION_EDITOR_WEBMODULE_DIALOG_RELOAD = TomcatUIPlugin.PLUGIN_ID + ".tdpr0008";

	public static final String CONFIGURATION_EDITOR_MAPPINGS = TomcatUIPlugin.PLUGIN_ID + ".tecm0000";
	public static final String CONFIGURATION_EDITOR_MAPPINGS_LIST = TomcatUIPlugin.PLUGIN_ID + ".tecm0002";
	public static final String CONFIGURATION_EDITOR_MAPPINGS_ADD = TomcatUIPlugin.PLUGIN_ID + ".tecm0004";
	public static final String CONFIGURATION_EDITOR_MAPPINGS_EDIT = TomcatUIPlugin.PLUGIN_ID + ".tecm0006";
	public static final String CONFIGURATION_EDITOR_MAPPINGS_REMOVE = TomcatUIPlugin.PLUGIN_ID + ".tecm0008";

	public static final String CONFIGURATION_EDITOR_MAPPING_DIALOG = TomcatUIPlugin.PLUGIN_ID + ".tdmm0000";
	public static final String CONFIGURATION_EDITOR_MAPPING_DIALOG_TYPE = TomcatUIPlugin.PLUGIN_ID + ".tdmm0002";
	public static final String CONFIGURATION_EDITOR_MAPPING_DIALOG_EXTENSION = TomcatUIPlugin.PLUGIN_ID + ".tdmm0004";

	public static final String CONFIGURATION_EDITOR_PORTS = TomcatUIPlugin.PLUGIN_ID + ".tecp0000";
	public static final String CONFIGURATION_EDITOR_PORTS_LIST = TomcatUIPlugin.PLUGIN_ID + ".tecp0002";
		
	public static final String RUNTIME_COMPOSITE = TomcatUIPlugin.PLUGIN_ID + ".twnr0000";

	public static final String SERVER_CLEAN_WORK_DIR = TomcatUIPlugin.PLUGIN_ID + ".tvcp0000";
	public static final String SERVER_CLEAN_WORK_DIR_TERMINATE = TomcatUIPlugin.PLUGIN_ID + ".tvcp0001";
}
