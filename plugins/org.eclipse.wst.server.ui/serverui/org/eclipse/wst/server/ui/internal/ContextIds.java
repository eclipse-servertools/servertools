package org.eclipse.wst.server.ui.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.wst.server.ui.ServerUICore;
/**
 * Context help id constants.
 */
public interface ContextIds {
	public static final String SELECT_SERVER_WIZARD = ServerUICore.PLUGIN_ID + ".swsi0000";
	public static final String SELECT_SERVER_EXISTING = ServerUICore.PLUGIN_ID + ".swsi0002";
	public static final String SELECT_SERVER_EXISTING_TABLE = ServerUICore.PLUGIN_ID + ".swsi0004";
	public static final String SELECT_SERVER_CREATE = ServerUICore.PLUGIN_ID + ".swsi0010";
	public static final String SELECT_SERVER_CREATE_TABLE = ServerUICore.PLUGIN_ID + ".swsi0012";
	public static final String SELECT_SERVER_PREFERENCE = ServerUICore.PLUGIN_ID + ".swsi0014";

	public static final String SELECT_CLIENT_WIZARD = ServerUICore.PLUGIN_ID + ".swsc0000";
	public static final String SELECT_CLIENT = ServerUICore.PLUGIN_ID + ".swsc0002";

	public static final String NEW_INSTANCE_WIZARD = ServerUICore.PLUGIN_ID + ".swni0000";
	public static final String NEW_INSTANCE_NAME = ServerUICore.PLUGIN_ID + ".swni0002";
	public static final String NEW_INSTANCE_FOLDER = ServerUICore.PLUGIN_ID + ".swni0004";
	public static final String NEW_INSTANCE_FACTORY = ServerUICore.PLUGIN_ID + ".swni0006";
	
	public static final String NEW_CONFIGURATION_WIZARD = ServerUICore.PLUGIN_ID + ".swnc0000";
	public static final String NEW_CONFIGURATION_NAME = ServerUICore.PLUGIN_ID + ".swnc0002";
	public static final String NEW_CONFIGURATION_FOLDER = ServerUICore.PLUGIN_ID + ".swnc0004";
	public static final String NEW_CONFIGURATION_FACTORY = ServerUICore.PLUGIN_ID + ".swnc0006";
	
	public static final String NEW_SERVER_WIZARD = ServerUICore.PLUGIN_ID + ".swns0000";
	public static final String NEW_SERVER_NAME = ServerUICore.PLUGIN_ID + ".swns0002";
	public static final String NEW_SERVER_FOLDER = ServerUICore.PLUGIN_ID + ".swns0004";
	public static final String NEW_SERVER_INSTANCE_FACTORY = ServerUICore.PLUGIN_ID + ".swns0006";
	
	public static final String LAUNCH_CONFIGURATION_SERVER_COMBO = ServerUICore.PLUGIN_ID + ".swsl0000";
	
	public static final String SELECT_TASK_WIZARD = ServerUICore.PLUGIN_ID + ".sstw0000";

	public static final String IMPORT_CONFIGURATION_WIZARD = ServerUICore.PLUGIN_ID + ".swic0000";
	public static final String IMPORT_CONFIGURATION_NAME = ServerUICore.PLUGIN_ID + ".swic0002";
	public static final String IMPORT_CONFIGURATION_FOLDER = ServerUICore.PLUGIN_ID + ".swic0004";
	public static final String IMPORT_CONFIGURATION_FACTORY = ServerUICore.PLUGIN_ID + ".swic0006";
	public static final String IMPORT_CONFIGURATION_LOCATION = ServerUICore.PLUGIN_ID + ".swic0008";
	public static final String IMPORT_CONFIGURATION_LOCATION_BROWSE = ServerUICore.PLUGIN_ID + ".swic0010";
	
	public static final String MODIFY_MODULES_COMPOSITE = ServerUICore.PLUGIN_ID + ".swmm0000";

	public static final String NEW_SERVER_PROJECT_DIALOG = ServerUICore.PLUGIN_ID + ".sdnp0000";

	public static final String TERMINATE_SERVER_DIALOG = ServerUICore.PLUGIN_ID + ".sdti0000";

	public static final String PREF_GENERAL = ServerUICore.PLUGIN_ID + ".spge0000";
	public static final String PREF_GENERAL_SHOW_PUBLISHING_DETAILS = ServerUICore.PLUGIN_ID + ".spge0010";
	public static final String PREF_GENERAL_PUBLISH_BEFORE_START = ServerUICore.PLUGIN_ID + ".spge0012";
	public static final String PREF_GENERAL_AUTO_RESTART = ServerUICore.PLUGIN_ID + ".spge0014";
	public static final String PREF_GENERAL_PUBLISHER = ServerUICore.PLUGIN_ID + ".spge0015";
	public static final String PREF_GENERAL_REPAIR = ServerUICore.PLUGIN_ID + ".spge0018";
	public static final String PREF_GENERAL_PROMPT_IRREVERSIBLE = ServerUICore.PLUGIN_ID + ".spge0020";
	public static final String PREF_GENERAL_CREATE_IN_WORKSPACE = ServerUICore.PLUGIN_ID + ".spge0022";
	public static final String PREF_GENERAL_SAVE_EDITORS = ServerUICore.PLUGIN_ID + ".spge0024";

	public static final String VIEW_CONFIG = ServerUICore.PLUGIN_ID + ".svcf0000";

	public static final String VIEW_CONTROL = ServerUICore.PLUGIN_ID + ".svcp0000";

	public static final String PUBLISH_DETAILS_DIALOG = ServerUICore.PLUGIN_ID + ".sdpd0000";
	public static final String PUBLISH_DETAILS_DIALOG_STATUS = ServerUICore.PLUGIN_ID + ".sdpd0002";
	public static final String PUBLISH_DETAILS_DIALOG_DETAILS_BUTTON = ServerUICore.PLUGIN_ID + ".sdpd0004";
	public static final String PUBLISH_DETAILS_DIALOG_DETAILS = ServerUICore.PLUGIN_ID + ".sdpd0006";

	public static final String PUBLISHER_DIALOG = ServerUICore.PLUGIN_ID + ".sdpr0000";
	public static final String PUBLISHER_DIALOG_TREE = ServerUICore.PLUGIN_ID + ".sdpr0002";
	public static final String PUBLISHER_DIALOG_SELECT_ALL = ServerUICore.PLUGIN_ID + ".sdpr0004";
	public static final String PUBLISHER_DIALOG_DESELECT_ALL = ServerUICore.PLUGIN_ID + ".sdpr0006";
	public static final String PUBLISHER_DIALOG_FILTER = ServerUICore.PLUGIN_ID + ".sdpr0008";
	public static final String PUBLISHER_DIALOG_FILTER_DIALOG = ServerUICore.PLUGIN_ID + ".sdpr0010";
	
	public static final String PROMPT_IRREVERSIBLE_DIALOG = ServerUICore.PLUGIN_ID + ".sdpi0000";
	
	public static final String EDITOR_OVERVIEW_PAGE = ServerUICore.PLUGIN_ID + ".seop0000";
}