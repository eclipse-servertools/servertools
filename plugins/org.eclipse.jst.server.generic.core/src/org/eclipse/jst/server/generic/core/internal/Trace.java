/***************************************************************************************************
 * Copyright (c) 2005, 2009 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal;


/**
 * Helper class to route trace output.
 */
public class Trace {
	/**
	 * Trace level CONFIG
	 */
	public static final byte CONFIG = 0;
	/**
	 * Trace level WARNING
	 */
	public static final byte WARNING = 1;
	/**
	 * Trace level SEVERE
	 */
	public static final byte SEVERE = 2;
	/**
	 * Trace level FINEST
	 */
	public static final byte FINEST = 3;
	/**
	 * Trace level FINER
	 */
	public static final byte FINER = 4;
	/**
	 * Trace level for performance logging
	 */
	public static final byte PERFORMANCE = 5; 
	
	/**
	 * Trace constructor comment.
	 */
	private Trace() {
		super();
	}
	
	/**
	 * Trace the given text.
	 * 
	 * @param level trace level constant
	 * @param s java.lang.String
	 */
	public static void trace(byte level, String s) {
		Trace.trace(level, s, null);
	}
	
	/**
	 * Trace the given message and exception.
	 * @param level trace level constant
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
	 * @param t throwable
	 */
	public static void trace(String s, Throwable t) {
		trace(FINEST, s, t);
	}
}
