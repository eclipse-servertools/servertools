/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;
/**
 * A launchable is a "reference" to a module artifact. The
 * module artifact is the actual resource on the server; the
 * launchable is the information necessary to access that
 * resource. Examples may include HTTP requests and JNDI names.
 * 
 * [issue: rename to avoid confusion with debug.ui.ILaunchable]
 * 
 * @since 1.0
 */
public interface ILaunchable {
	// no content
}