/*******************************************************************************
 * Copyright (c) 2005, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.ui.internal;

import org.eclipse.osgi.util.NLS;
/**
 * Translated messages.
 */
public class Messages extends NLS {
	public static String runtimeTypeTitle;
	public static String runtimeTypeDescription;
	public static String runtimeTypeName;
	public static String runtimeTypeLocation;
	public static String runtimeTypeSelectLocation;
	public static String runtimeTypeJRE;
	public static String browse;
	public static String runtimeTypeInstalledJREs;
	public static String runtimeTypeDefaultJRE;

	public static String classpathContainer;
	public static String classpathContainerDescription;
	public static String classpathContainerRuntimeList;
	public static String classpathContainerPageDescription;
	
	public static String profilerPrefsTitle;
	public static String profilerPrefsNoneRegistered;

	static {
		NLS.initializeMessages(JavaServerUIPlugin.PLUGIN_ID + ".internal.Messages", Messages.class);
	}
}
