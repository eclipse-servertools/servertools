/*******************************************************************************
 * Copyright (c) 2003, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
/**
 * Helper class to route trace output.
 */
public class Trace {
	/**
	 * Config trace event.
	 */
	public static final byte CONFIG = 0;
	
	/**
	 * Warning trace event.
	 */
	public static final byte WARNING = 1;
	
	/**
	 * Severe trace event.
	 */
	public static final byte SEVERE = 2;
	
	/**
	 * Finest trace event.
	 */
	public static final byte FINEST = 3;
	
	/**
	 * Parsing trace event.
	 */
	public static final byte PARSING = 4;

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
		trace(level, s, null);
	}
	
	/**
	 * Trace the given message and exception.
	 *
	 * @param level the trace level
	 * @param s a message
	 * @param t a throwable
	 */
	public static void trace(byte level, String s, Throwable t) {
		if (level == SEVERE)
			MonitorPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, MonitorPlugin.PLUGIN_ID, s, t));
		
		if (!MonitorPlugin.getInstance().isDebugging())
			return;
		
		System.out.println(MonitorPlugin.PLUGIN_ID + " " + System.currentTimeMillis() + " thread=["+Thread.currentThread().getId() +"]" + s);
		if (t != null)
			t.printStackTrace();
	}
}