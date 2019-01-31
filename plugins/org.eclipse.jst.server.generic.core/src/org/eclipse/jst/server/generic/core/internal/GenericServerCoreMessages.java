/***************************************************************************************************
 * Copyright (c) 2005, 2007 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get messages
 * 
 * @author Gorkem Ercan
 */
public class GenericServerCoreMessages extends NLS{

	private static final String RESOURCE_BUNDLE= "org.eclipse.jst.server.generic.core.internal.GenericServerCoreMessages";//$NON-NLS-1$
	public static String cancelNoPublish;
	public static String moduleNotCompatible;
	public static String errorPortInUse;
	public static String errorJRE;
	public static String errorNoServerType;
	public static String errorNoClasspath;
	public static String errorMissingClasspathEntry;
	public static String errorRemoveModuleAntpublisher;
	public static String errorPublishAntpublisher;
	public static String commandlineUnspecified;
	public static String workingdirUnspecified;
	public static String errorLaunchingExecutable;
	public static String missingServer;
	public static String externalStopLauncher;
	public static String debugPortUnspecified;
	public static String errorStartingExternalDebugging;
	public static String invalidPath;
	public static String runModeNotSupported;
	public static String unableToCreatePublisher;
	public static String canNotPublishDeletedModule;
	public static String antLauncherMissing;
    public static String attachingToExternalGenericServer;
    public static String verifyingExternalServerDebuggingLaunchAttributes;
    public static String externalServerDebugConnectorNotSpecified;
    public static String creatingExternalServerDebuggingSourceLocator;
    
	
	static{
		  NLS.initializeMessages(RESOURCE_BUNDLE, GenericServerCoreMessages.class);
	}





}
