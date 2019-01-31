/**********************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Tianchao Li (Tianchao.Li@gmail.com) - Start monitors by default 
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal;

import org.eclipse.osgi.util.NLS;
/**
 * Translated messages.
 */
public class Messages extends NLS {
	public static String editMonitor;
	public static String newMonitor;
	public static String localPort;
	public static String remoteHost;
	public static String remotePort;
	public static String remoteGroup;
	public static String connectionTimeout;
	public static String autoStart;
	public static String parseType;
	public static String errorDialogTitle;
	public static String preferenceDescription;
	public static String prefShowView;
	public static String monitorList;
	public static String columnStatus;
	public static String columnRemote;
	public static String columnType;
	public static String columnLocal;
	public static String columnAutoStart;
	public static String add;
	public static String edit;
	public static String remove;
	public static String start;
	public static String stop;
	public static String columns;
	public static String started;
	public static String stopped;
	public static String headerLabel;
	public static String imageViewInvalid;
	public static String xmlViewInvalid;
	public static String htmlViewInvalid;
	public static String viewDateFormat;
	public static String viewTime;
	public static String viewResponseTime;
	public static String viewType;
	public static String viewResponseTimeFormat;
	public static String viewSize;
	public static String viewEncoding;
	public static String viewResponse;
	public static String viewRequest;
	public static String viewRequestType;
	public static String viewResponseType;
	public static String actionSortByResponseTime;
	public static String viewSizeFormat;
	public static String actionClearToolTip;
	public static String actionShowHeader;
	public static String actionPin;
	public static String actionProperties;
	public static String yes;
	public static String no;
	public static String defaultEncodingOption;

	static {
		NLS.initializeMessages(MonitorUIPlugin.PLUGIN_ID + ".internal.Messages", Messages.class);
	}
}