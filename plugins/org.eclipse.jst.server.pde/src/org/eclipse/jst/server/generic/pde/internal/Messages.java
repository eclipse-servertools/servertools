/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.pde.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.jst.server.generic.pde.internal.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String labelJavaLaunchConfiguration;

	public static String labelExternalLaunchConfiguration;

	public static String labelServerName;

	public static String labelServerVendor;

	public static String labelServerVesion;

	public static String labelLaunchType;

	public static String labelStartBeforePublish;

	public static String pageTitle;

	public static String pageDescription;

	public static String windowTitleWizard;
}
