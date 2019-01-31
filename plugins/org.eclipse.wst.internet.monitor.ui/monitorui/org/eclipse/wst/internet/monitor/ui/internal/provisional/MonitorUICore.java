/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.provisional;

import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
/**
 * Main class for access to the monitor UI.
 * <p>
 * This class provides all functionality through static members. It is not intended
 * to be instantiated or subclassed.
 * </p>
 */
public final class MonitorUICore {
	/**
	 * Cannot create MonitorUICore - use static methods.
	 */
	private MonitorUICore() {
		// can't create
	}

	/**
	 * Returns an array of the requests currently being displayed in the TCP/IP
	 * monitor view.
	 *
	 * @return an array of requests
	 */
	public static Request[] getRequests() {
		return MonitorUIPlugin.getInstance().getRequests();
	}
}
