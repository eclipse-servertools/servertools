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
package org.eclipse.wst.server.ui.internal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugOptionsListener;

/**
 * Helper class to route trace output.
 */
public class Trace implements DebugOptionsListener {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm.ss.SSS"); //$NON-NLS-1$

	public static Set<String> logged = new HashSet<String>();

	// tracing enablement flags
	public static boolean CONFIG = false;
	public static boolean INFO = false;
	public static boolean WARNING = false;
	public static boolean SEVERE = false;
	public static boolean FINEST = false;
	public static boolean FINER = false;
	public static boolean PERFORMANCE = false;
	public static boolean EXTENSION_POINT = false;

	// tracing levels. One most exist for each debug option
	public final static String STRING_CONFIG = "/config"; //$NON-NLS-1$
	public final static String STRING_INFO = "/info"; //$NON-NLS-1$
	public final static String STRING_WARNING = "/warning"; //$NON-NLS-1$
	public final static String STRING_SEVERE = "/severe"; //$NON-NLS-1$
	public final static String STRING_FINEST = "/finest"; //$NON-NLS-1$
	public final static String STRING_FINER = "/finer"; //$NON-NLS-1$
	public final static String STRING_EXTENSION_POINT = "/extension_point"; //$NON-NLS-1$
	public final static String STRING_PERFORMANCE = "/performance"; //$NON-NLS-1$
	
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
		Trace.CONFIG = options.getBooleanOption(ServerUIPlugin.PLUGIN_ID + Trace.STRING_CONFIG, false);
		Trace.INFO = options.getBooleanOption(ServerUIPlugin.PLUGIN_ID + Trace.STRING_INFO, false);
		Trace.WARNING = options.getBooleanOption(ServerUIPlugin.PLUGIN_ID + Trace.STRING_WARNING, false);
		Trace.SEVERE = options.getBooleanOption(ServerUIPlugin.PLUGIN_ID + Trace.STRING_SEVERE, false);
		Trace.FINER = options.getBooleanOption(ServerUIPlugin.PLUGIN_ID + Trace.STRING_FINER, false);
		Trace.FINEST = options.getBooleanOption(ServerUIPlugin.PLUGIN_ID + Trace.STRING_FINEST, false);
		Trace.EXTENSION_POINT = options
				.getBooleanOption(ServerUIPlugin.PLUGIN_ID + Trace.STRING_EXTENSION_POINT, false);
		Trace.PERFORMANCE = options.getBooleanOption(ServerUIPlugin.PLUGIN_ID + Trace.STRING_PERFORMANCE, false);
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
		if (s == null) {
			return;
		}
		if (Trace.STRING_SEVERE.equals(level)) {
			if (!logged.contains(s)) {
				ServerUIPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, s, t));
				logged.add(s);
			}
		}
		if (ServerUIPlugin.getInstance().isDebugging()) {
			StringBuffer sb = new StringBuffer(ServerUIPlugin.PLUGIN_ID);
			sb.append(" "); //$NON-NLS-1$
			sb.append(level);
			sb.append(" "); //$NON-NLS-1$
			sb.append(sdf.format(new Date()));
			sb.append(" "); //$NON-NLS-1$
			sb.append(s);
			System.out.println(sb.toString());
			if (t != null) {
				t.printStackTrace();
			}
		}
	}
}