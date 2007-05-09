/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.preview.internal;
/**
 * Helper class to route trace output.
 */
public class Trace {
	/**
	 * Config tracing
	 */
	public static final byte CONFIG = 0;
	/**
	 * Warning tracing
	 */
	public static final byte WARNING = 1;
	/**
	 * Severe tracing
	 */
	public static final byte SEVERE = 2;
	/**
	 * Finest tracing
	 */
	public static final byte FINEST = 3;

	public static final byte PUBLISHING = 4;

	/**
	 * Trace constructor comment.
	 */
	private Trace() {
		super();
	}

	/**
	 * Trace the given text.
	 *
	 * @param level trace level
	 * @param s String
	 */
	public static void trace(byte level, String s) {
		Trace.trace(level, s, null);
	}

	/**
	 * Trace the given message and exception.
	 *
	 * @param level trace level
	 * @param s String
	 * @param t Throwable
	 */
	public static void trace(byte level, String s, Throwable t) {
		if (!PreviewPlugin.getInstance().isDebugging())
			return;
		
		System.out.println(PreviewPlugin.PLUGIN_ID + " " + s);
		if (t != null)
			t.printStackTrace();
	}
}