/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui;

import org.eclipse.wst.internet.monitor.core.Request;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
/**
 * Main class for access to the monitor UI.
 * <p>
 * This class provides all functionality through static members. It is not intended
 * to be instantiated or subclassed.
 * </p>
 * 
 * @since 1.0
 */
public final class MonitorUICore {
	/**
	 * Returns an array of the requests currently being displayed in the monitor.
	 *
	 * @return an array of requests
	 */
	public static Request[] getRequests() {
		return MonitorUIPlugin.getInstance().getRequests();
	}
}