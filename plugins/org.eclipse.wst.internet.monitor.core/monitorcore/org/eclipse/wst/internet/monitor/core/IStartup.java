/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core;
/**
 * An interface for the startup extension point.
 * Plug-ins that register a startup extension will be activated when the monitor
 * core plug-in initializes and have an opportunity to run code that can't be
 * implemented using the normal contribution mechanisms.
 * 
 * @since 1.0
 */
public interface IStartup {
	/**
	 * Will be called on monitor core startup.
	 */
	public void startup();
}