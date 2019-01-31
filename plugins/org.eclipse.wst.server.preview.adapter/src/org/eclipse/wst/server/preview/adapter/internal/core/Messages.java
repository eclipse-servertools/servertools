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
package org.eclipse.wst.server.preview.adapter.internal.core;

import org.eclipse.osgi.util.NLS;
/**
 * Translated messages.
 */
public class Messages extends NLS {
	public static String canModifyModules;
	public static String httpPort;

	public static String errorPortInUse;
	public static String errorPublish;

	static {
		NLS.initializeMessages(PreviewPlugin.PLUGIN_ID + ".internal.core.Messages", Messages.class);
	}
}