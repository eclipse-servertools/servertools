/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.internal;

import org.eclipse.jetty.util.log.Logger;

public class WTPLogger implements Logger {
	protected boolean debug = false;

	public void debug(String msg, Throwable t) {
		if (debug) {
			System.out.println(msg);
			t.printStackTrace();
		}
	}

	public void debug(String msg, Object arg1, Object arg2) {
		if (debug) {
			System.out.println(msg);
		}
	}

	public Logger getLogger(String name) {
		return this;
	}

	public void info(String msg, Object arg1, Object arg2) {
		if (debug) {
			System.out.println(msg);
		}
	}

	public boolean isDebugEnabled() {
		return debug;
	}

	public void setDebugEnabled(boolean debug) {
		this.debug = debug;
	}

	public void warn(String msg, Throwable t) {
		if (debug) {
			System.out.println(msg);
			t.printStackTrace();
		}
	}

	public void warn(String msg, Object arg1, Object arg2) {
		if (debug) {
			System.out.println(msg);
		}
	}
}
