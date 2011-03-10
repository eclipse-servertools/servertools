/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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
import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugOptionsListener;

/**
 * Helper class to route trace output.
 */
public class Trace implements DebugOptionsListener {

	// tracing enablement flags
	public static boolean CONFIG = false;
	public static boolean WARNING = false;
	public static boolean SEVERE = false;
	public static boolean FINEST = false;

	// tracing levels.  One most exist for each debug option
	public final static String STRING_CONFIG = "/config"; //$NON-NLS-1$
	public final static String STRING_FINEST = "/finest"; //$NON-NLS-1$
	public final static String STRING_WARNING = "/warning"; //$NON-NLS-1$
	public final static String STRING_SEVERE = "/severe"; //$NON-NLS-1$
	
	/**
	 * Trace constructor. This should never be explicitly called by clients and is used to register this class with the
	 * {@link DebugOptions} service.
	 */
	public Trace() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osgi.service.debug.DebugOptionsListener#optionsChanged(org.eclipse.osgi.service.debug.DebugOptions)
	 */
	public void optionsChanged(DebugOptions options) {
		Trace.CONFIG = options.getBooleanOption(JavaServerUIPlugin.PLUGIN_ID + Trace.STRING_CONFIG, false);
		Trace.WARNING = options.getBooleanOption(JavaServerUIPlugin.PLUGIN_ID + Trace.STRING_WARNING, false);
		Trace.SEVERE = options.getBooleanOption(JavaServerUIPlugin.PLUGIN_ID + Trace.STRING_SEVERE, false);
		Trace.FINEST = options.getBooleanOption(JavaServerUIPlugin.PLUGIN_ID + Trace.STRING_FINEST, false);
	}

	/**
	 * Trace the given message.
	 * 
	 * @param level
	 *            The tracing level.
	 * @param s
	 *            The message to trace
	 */
	public static void trace(final String level, final String s) {
		Trace.trace(level, s, null);
	}

	/**
	 * Trace the given message and exception.
	 * 
	 * @param level
	 *            The tracing level.
	 * @param s
	 *            The message to trace
	 * @param t
	 *            A {@link Throwable} to trace
	 */
	public static void trace(final String level, final String s, final Throwable t) {

		if (Trace.STRING_SEVERE.equals(level)) {
			JavaServerUIPlugin.getInstance().getLog()
					.log(new Status(IStatus.ERROR, JavaServerUIPlugin.PLUGIN_ID, s, t));
		}
		if (JavaServerUIPlugin.getInstance().isDebugging()) {
			System.out.println(JavaServerUIPlugin.PLUGIN_ID + " " + level + " " + s);
			if (t != null) {
				t.printStackTrace();
			}
		}
	}	
}