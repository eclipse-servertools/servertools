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

	public static final String NEW_INSTANCE_WIZARD = ServerUIPlugin.PLUGIN_ID + ".swni0000";
	public static final String NEW_INSTANCE_NAME = ServerUIPlugin.PLUGIN_ID + ".swni0002";
	public static final String NEW_INSTANCE_FOLDER = ServerUIPlugin.PLUGIN_ID + ".swni0004";
	public static final String NEW_INSTANCE_FACTORY = ServerUIPlugin.PLUGIN_ID + ".swni0006";
	
	public static final String NEW_CONFIGURATION_WIZARD = ServerUIPlugin.PLUGIN_ID + ".swnc0000";
	public static final String NEW_CONFIGURATION_NAME = ServerUIPlugin.PLUGIN_ID + ".swnc0002";
	public static final String NEW_CONFIGURATION_FOLDER = ServerUIPlugin.PLUGIN_ID + ".swnc0004";
	public static final String NEW_CONFIGURATION_FACTORY = ServerUIPlugin.PLUGIN_ID + ".swnc0006";
	
	public static final String NEW_SERVER_WIZARD = ServerUIPlugin.PLUGIN_ID + ".swns0000";
	public static final String NEW_SERVER_NAME = ServerUIPlugin.PLUGIN_ID + ".swns0002";
	public static final String NEW_SERVER_FOLDER = ServerUIPlugin.PLUGIN_ID + ".swns0004";
	public static final String NEW_SERVER_INSTANCE_FACTORY = ServerUIPlugin.PLUGIN_ID + ".swns0006";
	
	public static final String LAUNCH_CONFIGURATION_SERVER_COMBO = ServerUIPlugin.PLUGIN_ID + ".swsl0000";
	
	public static final String SELECT_TASK_WIZARD = ServerUIPlugin.PLUGIN_ID + ".sstw0000";

	public static final String IMPORT_CONFIGURATION_WIZARD = ServerUIPlugin.PLUGIN_ID + ".swic0000";
	public static final String IMPORT_CONFIGURATION_NAME = ServerUIPlugin.PLUGIN_ID + ".swic0002";
	public static final String IMPORT_CONFIGURATION_FOLDER = ServerUIPlugin.PLUGIN_ID + ".swic0004";
	public static final String IMPORT_CONFIGURATION_FACTORY = ServerUIPlugin.PLUGIN_ID + ".swic0006";
	public static final String IMPORT_CONFIGURATION_LOCATION = ServerUIPlugin.PLUGIN_ID + ".swic0008";
	public static final String IMPORT_CONFIGURATION_LOCATION_BROWSE = ServerUIPlugin.PLUGIN_ID + ".swic0010";
	
	public static final String MODIFY_MODULES_COMPOSITE = ServerUIPlugin.PLUGIN_ID + ".swmm0000";

	public static final String NEW_SERVER_PROJECT_DIALOG = ServerUIPlugin.PLUGIN_ID + ".sdnp0000";

	public static final String TERMINATE_SERVER_DIALOG = ServerUIPlugin.PLUGIN_ID + ".sdti0000";

	public static final String PREF_GENERAL = ServerUIPlugin.PLUGIN_ID + ".spge0000";
	public static final String PREF_GENERAL_SHOW_PUBLISHING_DETAILS = ServerUIPlugin.PLUGIN_ID + ".spge0010";
	public static final String PREF_GENERAL_PUBLISH_BEFORE_START = ServerUIPlugin.PLUGIN_ID + ".spge0012";
	public static final String PREF_GENERAL_AUTO_RESTART = ServerUIPlugin.PLUGIN_ID + ".spge0014";
	public static final String PREF_GENERAL_SHOW_ON_ACTIVITY = ServerUIPlugin.PLUGIN_ID + ".spge0016";
	public static final String PREF_GENERAL_REPAIR = ServerUIPlugin.PLUGIN_ID + ".spge0018";
	public static final String PREF_GENERAL_PROMPT_IRREVERSIBLE = ServerUIPlugin.PLUGIN_ID + ".spge0020";
	public static final String PREF_GENERAL_CREATE_IN_WORKSPACE = ServerUIPlugin.PLUGIN_ID + ".spge0022";
	public static final String PREF_GENERAL_SAVE_EDITORS = ServerUIPlugin.PLUGIN_ID + ".spge0024";

	public static final String VIEW_CONFIG = ServerUIPlugin.PLUGIN_ID + ".svcf0000";

	public static final String VIEW_CONTROL = ServerUIPlugin.PLUGIN_ID + ".svcp0000";

	public static final String PUBLISH_DETAILS_DIALOG = ServerUIPlugin.PLUGIN_ID + ".sdpd0000";
	public static final String PUBLISH_DETAILS_DIALOG_STATUS = ServerUIPlugin.PLUGIN_ID + ".sdpd0002";
	public static final String PUBLISH_DETAILS_DIALOG_DETAILS_BUTTON = ServerUIPlugin.PLUGIN_ID + ".sdpd0004";
	public static final String PUBLISH_DETAILS_DIALOG_DETAILS = ServerUIPlugin.PLUGIN_ID + ".sdpd0006";

	public static final String PUBLISHER_DIALOG = ServerUIPlugin.PLUGIN_ID + ".sdpr0000";
	public static final String PUBLISHER_DIALOG_TREE = ServerUIPlugin.PLUGIN_ID + ".sdpr0002";
	public static final String PUBLISHER_DIALOG_SELECT_ALL = ServerUIPlugin.PLUGIN_ID + ".sdpr0004";
	public static final String PUBLISHER_DIALOG_DESELECT_ALL = ServerUIPlugin.PLUGIN_ID + ".sdpr0006";
	public static final String PUBLISHER_DIALOG_FILTER = ServerUIPlugin.PLUGIN_ID + ".sdpr0008";
	public static final String PUBLISHER_DIALOG_FILTER_DIALOG = ServerUIPlugin.PLUGIN_ID + ".sdpr0010";
	
	public static final String PROMPT_IRREVERSIBLE_DIALOG = ServerUIPlugin.PLUGIN_ID + ".sdpi0000";
	
	public static final String EDITOR_OVERVIEW_PAGE = ServerUIPlugin.PLUGIN_ID + ".seop0000";
}