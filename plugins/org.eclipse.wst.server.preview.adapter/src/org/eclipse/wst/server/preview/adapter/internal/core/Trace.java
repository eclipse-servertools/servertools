/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.adapter.internal.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
/**
 * Helper class to route trace output.
 */
public class Trace {
	public static byte CONFIG = 0;
	public static byte WARNING = 1;
	public static byte SEVERE = 2;
	public static byte FINEST = 3;
	public static byte FINER = 4;

	private static final String[] levelNames = new String[] { "CONFIG   ", "WARNING  ",
		"SEVERE   ", "FINER    ", "FINEST   " };

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
	 * @param level the trace level
	 * @param s a message
	 */
	public static void trace(byte level, String s) {
		Trace.trace(level, s, null);
	}

	/**
	 * Trace the given message and exception.
	 * 
	 * @param level the trace level
	 * @param s a message
	 * @param t a throwable
	 */
	public static void trace(byte level, String s, Throwable t) {
		if (s == null)
			return;
		
		if (level == SEVERE)
			PreviewPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, PreviewPlugin.PLUGIN_ID, s, t));
		
		if (!PreviewPlugin.getInstance().isDebugging())
			return;
		
		StringBuffer sb = new StringBuffer(PreviewPlugin.PLUGIN_ID);
		sb.append(" ");
		sb.append(levelNames[level]);
		sb.append(" ");
		sb.append(sdf.format(new Date()));
		sb.append(" ");
		sb.append(s);
		// Platform.getDebugOption(ServerCore.PLUGIN_ID + "/" + "resources");

		System.out.println(sb.toString());
		if (t != null)
			t.printStackTrace();
	}
}