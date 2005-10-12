/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;
/**
 * Context help id constants.
 */
public interface ContextIds {
	public static final String SELECT_SERVER_WIZARD = ServerUIPlugin.PLUGIN_ID + ".swsi0000";
	public static final String SELECT_SERVER_EXISTING = ServerUIPlugin.PLUGIN_ID + ".swsi0002";
	public static final String SELECT_SERVER_EXISTING_TABLE = ServerUIPlugin.PLUGIN_ID + ".swsi0004";
	public static final String SELECT_SERVER_CREATE = ServerUIPlugin.PLUGIN_ID + ".swsi0010";
	public static final String SELECT_SERVER_CREATE_TABLE = ServerUIPlugin.PLUGIN_ID + ".swsi0012";
	public static final String SELECT_SERVER_PREFERENCE = ServerUIPlugin.PLUGIN_ID + ".swsi0014";

	public static final String SELECT_CLIENT_WIZARD = ServerUIPlugin.PLUGIN_ID + ".swsc0000";
	public static final String SELECT_CLIENT = ServerUIPlugin.PLUGIN_ID + ".swsc0002";

	public static final String NEW_SERVER_WIZARD = ServerUIPlugin.PLUGIN_ID + ".swns0000";
	public static final String NEW_SERVER_TYPE = ServerUIPlugin.PLUGIN_ID + ".swns0006";

	public static final String LAUNCH_CONFIGURATION_SERVER_COMBO = ServerUIPlugin.PLUGIN_ID + ".swsl0000";

	public static final String SELECT_TASK_WIZARD = ServerUIPlugin.PLUGIN_ID + ".sstw0000";

	/*public static final String IMPORT_CONFIGURATION_WIZARD = ServerUIPlugin.PLUGIN_ID + ".swic0000";
	public static final String IMPORT_CONFIGURATION_NAME = ServerUIPlugin.PLUGIN_ID + ".swic0002";
	public static final String IMPORT_CONFIGURATION_FOLDER = ServerUIPlugin.PLUGIN_ID + ".swic0004";
	public static final String IMPORT_CONFIGURATION_FACTORY = ServerUIPlugin.PLUGIN_ID + ".swic0006";
	public static final String IMPORT_CONFIGURATION_LOCATION = ServerUIPlugin.PLUGIN_ID + ".swic0008";
	public static final String IMPORT_CONFIGURATION_LOCATION_BROWSE = ServerUIPlugin.PLUGIN_ID + ".swic0010";*/
	
	public static final String MODIFY_MODULES_COMPOSITE = ServerUIPlugin.PLUGIN_ID + ".swmm0000";

	public static final String TERMINATE_SERVER_DIALOG = ServerUIPlugin.PLUGIN_ID + ".sdti0000";

	public static final String PREF_GENERAL = ServerUIPlugin.PLUGIN_ID + ".spge0000";
	public static final String PREF_GENERAL_AUTOPUBLISH_LOCAL = ServerUIPlugin.PLUGIN_ID + ".spge0002";
	public static final String PREF_GENERAL_AUTOPUBLISH_REMOTE = ServerUIPlugin.PLUGIN_ID + ".spge0006";
	public static final String PREF_GENERAL_PUBLISH_BEFORE_START = ServerUIPlugin.PLUGIN_ID + ".spge0012";
	public static final String PREF_GENERAL_AUTO_RESTART = ServerUIPlugin.PLUGIN_ID + ".spge0014";
	public static final String PREF_GENERAL_PROMPT_IRREVERSIBLE = ServerUIPlugin.PLUGIN_ID + ".spge0020";
	public static final String PREF_GENERAL_SHOW_ON_ACTIVITY = ServerUIPlugin.PLUGIN_ID + ".spge0022";
	public static final String PREF_GENERAL_SAVE_EDITORS = ServerUIPlugin.PLUGIN_ID + ".spge0024";
	public static final String PREF_GENERAL_TIMEOUT_DELAY = ServerUIPlugin.PLUGIN_ID + ".spge0026";

	public static final String VIEW_SERVERS = ServerUIPlugin.PLUGIN_ID + ".svcp0000";

	public static final String PUBLISH_DETAILS_DIALOG = ServerUIPlugin.PLUGIN_ID + ".sdpd0000";
	public static final String PUBLISH_DETAILS_DIALOG_STATUS = ServerUIPlugin.PLUGIN_ID + ".sdpd0002";
	public static final String PUBLISH_DETAILS_DIALOG_DETAILS_BUTTON = ServerUIPlugin.PLUGIN_ID + ".sdpd0004";
	public static final String PUBLISH_DETAILS_DIALOG_DETAILS = ServerUIPlugin.PLUGIN_ID + ".sdpd0006";

	public static final String PROMPT_IRREVERSIBLE_DIALOG = ServerUIPlugin.PLUGIN_ID + ".sdpi0000";

	public static final String EDITOR_OVERVIEW_PAGE = ServerUIPlugin.PLUGIN_ID + ".seop0000";
	public static final String EDITOR_SERVER = ServerUIPlugin.PLUGIN_ID + ".seop0002";
	public static final String EDITOR_CONFIGURATION = ServerUIPlugin.PLUGIN_ID + ".seop0004";
	public static final String EDITOR_HOSTNAME = ServerUIPlugin.PLUGIN_ID + ".seop0006";
	public static final String EDITOR_RUNTIME = ServerUIPlugin.PLUGIN_ID + ".seop0008";
	public static final String EDITOR_AUTOPUBLISH_DEFAULT = ServerUIPlugin.PLUGIN_ID + ".seop0010";
	public static final String EDITOR_AUTOPUBLISH_OVERRIDE = ServerUIPlugin.PLUGIN_ID + ".seop0012";
	public static final String EDITOR_AUTOPUBLISH_DISABLE = ServerUIPlugin.PLUGIN_ID + ".seop0016";

	public static final String AUDIO_PREFERENCES = ServerUIPlugin.PLUGIN_ID + ".aupr0000";
	public static final String AUDIO_PREFERENCES_ENABLE = ServerUIPlugin.PLUGIN_ID + ".aupr0002";
	public static final String AUDIO_PREFERENCES_VOLUME = ServerUIPlugin.PLUGIN_ID + ".aupr0004";
	public static final String AUDIO_PREFERENCES_SOUNDS_TABLE = ServerUIPlugin.PLUGIN_ID + ".aupr0006";
	public static final String AUDIO_PREFERENCES_PLAY = ServerUIPlugin.PLUGIN_ID + ".aupr0008";
	public static final String AUDIO_PREFERENCES_BROWSE = ServerUIPlugin.PLUGIN_ID + ".aupr0010";
	public static final String AUDIO_PREFERENCES_RESET = ServerUIPlugin.PLUGIN_ID + ".aupr0012";
}