/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;
/**
 * An interface for the startup extension point.
 * Plug-ins that register a startup extension will be activated when the server
 * core plug-in initializes and have an opportunity to run code that can't be
 * implemented using the normal contribution mechanisms.
 * 
 * @since 1.0
 */
public abstract class IStartup {
	/**
	 * Will be called on server core startup.
	 */
	public abstract void startup();
}