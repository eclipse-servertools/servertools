/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
package org.eclipse.wst.internet.monitor.core.internal;
/**
 * An interface for the startup extension point.
 * Plug-ins that register a startup extension will be activated when the monitor
 * core plug-in initializes and have an opportunity to run code that can't be
 * implemented using the normal contribution mechanisms.
 */
public interface IStartup {
	/**
	 * Will be called on monitor core startup.
	 */
	public void startup();
}
