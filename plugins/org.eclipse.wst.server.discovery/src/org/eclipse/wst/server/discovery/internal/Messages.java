/**********************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.discovery.internal;

import org.eclipse.osgi.util.NLS;
/**
 * Translated messages.
 */
public class Messages extends NLS {
	public static String dialogTitle;
	public static String viewInitializing;

	public static String wizExtensionTitle;
	public static String wizExtensionDescription;
	public static String wizExtensionMessage;

	public static String discoverSearching;
	public static String discoverLocalConfiguration;
	public static String discoverSiteError;

	public static String installConfirm;
	public static String installJobName;
	public static String installPromptRestart;

	static {
		NLS.initializeMessages(Activator.PLUGIN_ID + ".internal.Messages", Messages.class);
	}
}