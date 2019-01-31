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
package org.eclipse.wst.server.http.core.internal;

import org.eclipse.osgi.util.NLS;
/**
 * Translated messages.
 */
public class Messages extends NLS {
	public static String errorPublish;
	public static String canModifyModules;
	public static String httpPort;
	public static String actionModifyPort;
	public static String actionModifyPrefixURL;
	public static String actionModifyPublishing;

	public static String errorPortInUse;

	static {
		NLS.initializeMessages(HttpCorePlugin.PLUGIN_ID + ".internal.Messages", Messages.class);
	}
}