/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.internal.ui;
/**
 * Context help id constants.
 */
public interface ContextIds {
	public static final String JAVA_CLASSPATH = JavaServerUIPlugin.PLUGIN_ID + ".jvcp0000";
	public static final String JAVA_CLASSPATH_TABLE = JavaServerUIPlugin.PLUGIN_ID + ".jvcp0002";
	public static final String JAVA_CLASSPATH_UP = JavaServerUIPlugin.PLUGIN_ID + ".jvcp0004";
	public static final String JAVA_CLASSPATH_DOWN = JavaServerUIPlugin.PLUGIN_ID + ".jvcp0006";
	public static final String JAVA_CLASSPATH_ADD_EXTERNAL_JAR = JavaServerUIPlugin.PLUGIN_ID + ".jvcp0008";
	public static final String JAVA_CLASSPATH_ADD_EXTERNAL_FOLDER = JavaServerUIPlugin.PLUGIN_ID + ".jvcp0009";
	public static final String JAVA_CLASSPATH_ADD_VARIABLE = JavaServerUIPlugin.PLUGIN_ID + ".jvcp0011";
	public static final String JAVA_CLASSPATH_ADD_STRING = JavaServerUIPlugin.PLUGIN_ID + ".jvcp0013";
	public static final String JAVA_CLASSPATH_EDIT = JavaServerUIPlugin.PLUGIN_ID + ".jvcp0015";
	public static final String JAVA_CLASSPATH_REMOVE = JavaServerUIPlugin.PLUGIN_ID + ".jvcp0012";

	public static final String JAVA_PATH = JavaServerUIPlugin.PLUGIN_ID + ".jvpt0000";
	public static final String JAVA_PATH_FIELD = JavaServerUIPlugin.PLUGIN_ID + ".jvpt0002";
	public static final String JAVA_PATH_APPEND = JavaServerUIPlugin.PLUGIN_ID + ".jvpt0004";
	public static final String JAVA_PATH_PREPEND = JavaServerUIPlugin.PLUGIN_ID + ".jvpt0006";
	public static final String JAVA_PATH_REPLACE = JavaServerUIPlugin.PLUGIN_ID + ".jvpt0008";

	public static final String JAVA_SYSTEM_PROPERTY = JavaServerUIPlugin.PLUGIN_ID + ".jvsp0000";
	public static final String JAVA_SYSTEM_PROPERTY_TABLE = JavaServerUIPlugin.PLUGIN_ID + ".jvsp0002";
	public static final String JAVA_SYSTEM_PROPERTY_ADD = JavaServerUIPlugin.PLUGIN_ID + ".jvsp0004";
	public static final String JAVA_SYSTEM_PROPERTY_EDIT = JavaServerUIPlugin.PLUGIN_ID + ".jvsp0006";
	public static final String JAVA_SYSTEM_PROPERTY_REMOVE = JavaServerUIPlugin.PLUGIN_ID + ".jvsp0008";

	public static final String JAVA_SYSTEM_PROPERTY_DIALOG = JavaServerUIPlugin.PLUGIN_ID + ".jvpd0000";
	public static final String JAVA_SYSTEM_PROPERTY_DIALOG_NAME = JavaServerUIPlugin.PLUGIN_ID + ".jvpd0002";
	public static final String JAVA_SYSTEM_PROPERTY_DIALOG_VALUE = JavaServerUIPlugin.PLUGIN_ID + ".jvpd0004";

	public static final String JAVA_CLASSPATH_VARIABLE_DIALOG = JavaServerUIPlugin.PLUGIN_ID + ".jvvd0000";
	public static final String JAVA_CLASSPATH_VARIABLE_DIALOG_VARIABLE = JavaServerUIPlugin.PLUGIN_ID + ".jvvd0002";
	public static final String JAVA_CLASSPATH_VARIABLE_DIALOG_EXTENSION = JavaServerUIPlugin.PLUGIN_ID + ".jvvd0004";
	public static final String JAVA_CLASSPATH_VARIABLE_DIALOG_EXTENSION_BROWSE = JavaServerUIPlugin.PLUGIN_ID + ".jvvd0006";

	public static final String JAVA_CLASSPATH_STRING_DIALOG = JavaServerUIPlugin.PLUGIN_ID + ".jvsd0000";
	public static final String JAVA_CLASSPATH_STRING_DIALOG_PATH = JavaServerUIPlugin.PLUGIN_ID + ".jvsd0002";

	public static final String JAVA_VM_ARGUMENTS = JavaServerUIPlugin.PLUGIN_ID + ".jvvm0000";
	
	public static final String RUNTIME_TARGET_COMPOSITE = JavaServerUIPlugin.PLUGIN_ID + ".jvrt0000";
}