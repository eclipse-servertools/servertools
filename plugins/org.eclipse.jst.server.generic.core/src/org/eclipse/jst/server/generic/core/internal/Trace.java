package org.eclipse.jst.server.generic.core.internal;


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
/**
 * Helper class to route trace output.
 */
public class Trace {
	public static byte CONFIG = 0;
	public static byte WARNING = 1;
	public static byte SEVERE = 2;
	public static byte FINEST = 3;
	public static byte FINER = 4;
	
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
		Trace.trace(level, s, null);
	}
	
	/**
	 * Trace the given message and exception.
	 *
	 * @param s java.lang.String
	 * @param t java.lang.Throwable
	 */
	public static void trace(byte level, String s, Throwable t) {
		if (!CorePlugin.getDefault().isDebugging())
			return;

		System.out.println(s);
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
	 * Trace the given exception.
	 *
	 * @param s java.lang.String
	 * @param e java.lang.Throwable
	 */
	public static void trace(String s, Throwable t) {
		trace(FINEST, s, t);
	}
}