/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.resources;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
/**
 * A module file is a leaf resource in a module. 
 * Files contains data.
 * <p>
 * This interface is not intended to be implemented by clients
 * other than module factories.
 * </p>
 * <p>
 * [issue: See issues on IModuleResource about how to get rid
 * of this interface.]
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IModuleFile extends IModuleResource {

	/**
	 * Returns an open input stream on the contents of this file.
	 * The client is responsible for closing the stream when finished.
	 *
	 * @return an input stream containing the contents of the file
	 * @exception CoreException if this method fails
	 */
	public InputStream getContents() throws CoreException;
}