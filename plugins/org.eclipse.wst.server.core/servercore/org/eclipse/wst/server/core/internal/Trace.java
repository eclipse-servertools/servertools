/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Helper class to route trace output.
 */
public class Trace {
	public static int CONFIG = 0;
	public static int INFO = 1;
	public static int WARNING = 2;
	public static int SEVERE = 3;
	public static int FINER = 4;
	public static int FINEST = 5;
	
	public static int RESOURCES = 6;
	public static int EXTENSION_POINT = 7;
	public static int LISTENERS = 8;
	public static int RUNTIME_TARGET = 9;
	public static int PERFORMANCE = 10;

	private static final String[] levelNames = new String[] {
		"CONFIG   ", "INFO     ", "WARNING  ", "SEVERE   ", "FINER    ", "FINEST   ",
		"RESOURCES", "EXTENSION", "LISTENERS", "TARGET   ", "PERF     "};
	private static final String spacer = "                                   ";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm.ss.SSS");

	protected static int pluginLength = -1;

	/**
	 * Trace constructor comment.
	 */
	private Trace() {
		super();
	}

	/**
	 * Trace the given text.
	 *
	 * @param s java.lang.String
	 */
	public static void trace(int level, String s) {
		trace(level, s, null);
	}

	/**
	 * Trace the given message and exception.
	 *
	 * @param s java.lang.String
	 * @param t java.lang.Throwable
	 */
	public static void trace(int level, String s, Throwable t) {
		trace(ServerPlugin.PLUGIN_ID, level, s, t);
	}

	/**
	 * Trace the given message and exception.
	 *
	 * @param s java.lang.String
	 * @param t java.lang.Throwable
	 */
	public static void trace(String pluginId, int level, String s, Throwable t) {
		if (pluginId == null || s == null)
			return;

		if (!ServerPlugin.getInstance().isDebugging())
			return;
		
		StringBuffer sb = new StringBuffer(pluginId);
		if (pluginId.length() > pluginLength)
			pluginLength = pluginId.length();
		else if (pluginId.length() < pluginLength)
			sb.append(spacer.substring(0, pluginLength - pluginId.length()));
		sb.append(" ");
		sb.append(levelNames[level]);
		sb.append(" ");
		sb.append(sdf.format(new Date()));
		sb.append(" ");
		sb.append(s);
		//Platform.getDebugOption(ServerCore.PLUGIN_ID + "/" + "resources");

		System.out.println(sb.toString());
		if (t != null)
			t.printStackTrace();
	}

	/**
	 * Trace the given text.
	 *
	 * @param s java.lang.String
	 */
	public static void trace(String s) {
		trace(s, null);
	}

	/**
	 * Trace the given message and exception.
	 *
	 * @param s java.lang.String
	 * @param t java.lang.Throwable
	 */
	public static void trace(String s, Throwable t) {
		trace(FINER, s, t);
	}
}