/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;
/**
 * A launchable is a "reference" to a module object. The
 * module object is the actual resource on the server; the
 * launchable is the information necessary to access that
 * resource. Examples may include HTTP requests and JNDI names.
 */
public interface ILaunchable {
	/**
	 * Returns the id of this launchable. Each known launchable has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the launchable id
	 */
	public String getId();
}