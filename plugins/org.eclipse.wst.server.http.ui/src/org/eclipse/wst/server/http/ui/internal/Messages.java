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
package org.eclipse.wst.server.http.ui.internal;

import org.eclipse.osgi.util.NLS;
/**
 * Translated messages.
 */
public class Messages extends NLS {
	public static String browse;
	public static String runtimeName;
	public static String port;
	public static String URLPrefix;
	public static String shouldPublish;
	public static String publishDir;
	public static String selectInstallDir;
	public static String wizardDescription;
	public static String wizardDuplicateName;
	public static String wizardMissingRuntimeName;
	public static String wizardTitle;
	public static String editorSectionTitle;
	public static String editorSectionDescription;
	public static String editorURLPrefix;
	public static String editorPort;
	public static String editorShouldPublish;

	static {
		NLS.initializeMessages(HttpUIPlugin.PLUGIN_ID + ".internal.Messages", Messages.class);
	}
}