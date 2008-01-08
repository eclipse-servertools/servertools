/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
/**
 * Temporary interface to help the web services team.
 */
public interface IModulePublishHelper {
	/**
	 * Returns the path that the module is published to, or null if the module is not
	 * published to the server or the location is unknown.
	 * 
	 * @param module a module on the server 
	 * @return the path that the module is published to, or <code>null</code> if not
	 *   a valid module or the location is unknown
	 */
	public IPath getPublishDirectory(IModule[] module);
}
