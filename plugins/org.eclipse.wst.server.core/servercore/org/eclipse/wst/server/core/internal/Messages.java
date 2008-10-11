/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.osgi.util.NLS;
/**
 * Translated messages.
 */
public class Messages extends NLS {
	public static String publishing;
	public static String errorRuntimeName;
	public static String errorDuplicateRuntimeName;
	public static String errorSaving;
	public static String errorLoading;
	public static String errorCannotAddModule;
	public static String defaultVendor;
	public static String defaultVersion;
	public static String savingTask;
	public static String taskPerforming;
	public static String deletingTask;
	public static String defaultServerProjectName;
	public static String defaultRuntimeName;
	public static String defaultRuntimeName2;
	public static String defaultServerName;
	public static String defaultServerName2;
	public static String defaultServerName3;
	public static String defaultServerName4;
	public static String taskModifyModules;
	public static String errorWorkingCopyTimestamp;
	public static String errorPublishStarting;
	public static String errorPublishNoRuntime;
	public static String errorPublishNoConfiguration;
	public static String errorNoConfiguration;
	public static String publishingStatusInfo;
	public static String publishingStatusWarning;
	public static String publishingStatusError;
	public static String errorPublishing;
	public static String publishingStop;
	public static String publishingModule;
	public static String publishedModule;
	public static String errorStartTimeout;
	public static String errorStartFailed;
	public static String errorStopFailed;
	public static String errorRestartTimeout;
	public static String errorRestartFailed;
	public static String errorModuleRestartFailed;
	public static String errorInstallingServer;
	public static String errorInstallingServerFeature;
	public static String errorRestartModule;
	public static String canStartErrorState;
	public static String errorLaunchMode;
	public static String errorRestartNotStarted;
	public static String errorStopAlreadyStopped;
	public static String moduleTypeUnknown;
	public static String jobStarting;
	public static String jobStopping;
	public static String jobRestarting;
	public static String jobUpdateServer;
	public static String jobInstallingRuntime;
	public static String errorMissingAdapter;
	
	public static String errorCopyingFile;
	public static String errorDeleting;
	public static String errorMkdir;
	public static String copyingTask;
	public static String errorReading;
	public static String errorCreatingZipFile;
	public static String errorRename;
	public static String errorNotADirectory;

	public static String errorNoRuntime;
	public static String errorFacet;

	static {
		NLS.initializeMessages(ServerPlugin.PLUGIN_ID + ".internal.Messages", Messages.class);
	}
}