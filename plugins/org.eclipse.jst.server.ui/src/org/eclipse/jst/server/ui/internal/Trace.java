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
package org.eclipse.jst.server.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
/**
 * Helper class to route trace output.
 */
public class Trace {
	public static final byte CONFIG = 0;
	public static final byte WARNING = 1;
	public static final byte SEVERE = 2;
	public static final byte FINEST = 3;

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
	public static void trace(byte level, String s) {
		Trace.trace(level, s, null);
	}
	
	/**
	 * Trace the given message and exception.
	 *
	 * @param level a trace level
	 * @param s a message
	 * @param t a throwable
	 */
	public static void trace(byte level, String s, Throwable t) {
		if (level == SEVERE)
			JavaServerUIPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, JavaServerUIPlugin.PLUGIN_ID, s, t));
		
		if (!JavaServerUIPlugin.getInstance().isDebugging())
			return;
		
		System.out.println(JavaServerUIPlugin.PLUGIN_ID + " " + s);
		if (t != null)
			t.printStackTrace();
	}
}