/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
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
	public static String errorInternalCactus;

	public static String LaunchTestAction_message_selectConfiguration;
	public static String LaunchTestAction_message_selectDebugConfiguration;
	public static String LaunchTestAction_message_selectRunConfiguration;

	public static String NewServletTestCaseWizard_WindowTitle;
	public static String NewServletTestCaseWizard_ErrorMessageTitleMissingLibrary;
	public static String NewServletTestCaseWizard_ErrorMessageMissingType;
	public static String NewServletTestCaseWizard_ErrorMessageMissingLibrary;
	public static String NewServletTestCaseWizard_ErrorTitleNew;
	public static String NewServletTestCaseWizard_ErrorTitleCreateOfElementFailed;
	public static String NewServletTestCaseWizard_ErrorMessageSeeErrorLog;

	static {
		NLS.initializeMessages(JavaServerUIPlugin.PLUGIN_ID + ".internal.Messages", Messages.class);
	}
}