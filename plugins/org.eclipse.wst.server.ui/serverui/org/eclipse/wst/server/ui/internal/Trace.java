/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Helper class to route trace output.
 */
public class Trace {
	public static byte CONFIG = 0;
	public static byte INFO = 1;
	public static byte WARNING = 2;
	public static byte SEVERE = 3;
	public static byte FINEST = 4;
	public static byte FINER = 5;
	public static byte PERFORMANCE = 6;
	public static byte EXTENSION_POINT = 7;
	
	protected static int pluginLength = -1;
	
	private static final String[] levelNames = new String[] {
		"CONFIG ", "INFO   ", "WARNING", "SEVERE ", "FINER  ", "FINEST ", "PERF   ", "EXTENSION"};
	private static final String spacer = "                                   ";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm.ss.SSS");

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
	public static void trace(byte level, String s) {
		trace(level, s, null);
	}
	
	/**
	 * Trace the given message and exception.
	 *
	 * @param s java.lang.String
	 * @param t java.lang.Throwable
	 */
	public static void trace(byte level, String s, Throwable t) {
		if (!ServerUIPlugin.getInstance().isDebugging())
			return;

		String pluginId = ServerUIPlugin.PLUGIN_ID;
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
		System.out.println(sb.toString());
		if (t != null)
			t.printStackTrace();
	}
}