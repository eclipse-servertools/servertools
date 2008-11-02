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
	public static String installableServerLocal;
	public static String installableServerSearching;
	public static String wizNewInstallableServerConfirm;
	public static String wizNewInstallableServerJob;
	public static String defaultDialogTitle;
	public static String wizNewInstallableServerRestart;
	public static String wizNewInstallableServerTitle;
	public static String wizNewInstallableServerDescription;
	public static String wizLicenseTitle;
	public static String wizLicenseDescription;
	public static String wizNewInstallableServerMessage;
	public static String wizLicenseAccept;
	public static String wizLicenseDecline;
	public static String wizLicenseNone;
	public static String wizNewInstallableServerSiteError;
	public static String viewInitializing;

	static {
		NLS.initializeMessages(Activator.PLUGIN_ID + ".internal.Messages", Messages.class);
	}
}