/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
/**
 * Helper class to route trace output.
 */
public class Trace {
	public static final byte CONFIG = 0;
	public static final byte INFO = 1;
	public static final byte WARNING = 2;
	public static final byte SEVERE = 3;
	public static final byte FINER = 4;
	public static final byte FINEST = 5;
	
	public static final byte RESOURCES = 6;
	public static final byte EXTENSION_POINT = 7;
	public static final byte LISTENERS = 8;
	public static final byte RUNTIME_TARGET = 9;
	public static final byte PERFORMANCE = 10;
	public static final byte PUBLISHING = 11;

	private static final String[] levelNames = new String[] {
		"CONFIG   ", "INFO     ", "WARNING  ", "SEVERE   ", "FINER    ", "FINEST   ",
		"RESOURCES", "EXTENSION", "LISTENERS", "TARGET   ", "PERF     ", "PUBLISH  "};
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
	 * @param level a trace level
	 * @param s a message
	 */
	public static void trace(int level, String s) {
		trace(level, s, null);
	}

	/**
	 * Trace the given message and exception.
	 *
	 * @param level a trace level
	 * @param s a message
	 * @param t a throwable
	 */
	public static void trace(int level, String s, Throwable t) {
		trace(ServerPlugin.PLUGIN_ID, level, s, t);
	}

	/**
	 * Trace the given message and exception.
	 *
	 * @param level a trace level
	 * @param s a message
	 * @param t a throwable
	 */
	private static void trace(String pluginId, int level, String s, Throwable t) {
		if (pluginId == null || s == null)
			return;
		
		if (level == SEVERE)
			ServerPlugin.log(new Status(IStatus.ERROR, pluginId, s, t));
		
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
}