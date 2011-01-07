/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import java.net.URL;

/**
 * An interface for providing URLs for a module published
 * to the server.
 * 
 * @since 1.0
 */
public interface IURLProvider2 extends IURLProvider {

	/**
	 * Returns a URL (e.g. "http://localhost:8080/myProject/myServlet") that can be used to launch
	 * a browser pointing to a specific resource inside a module.    
	 * @return
	 */
	public URL getLaunchableURL();

}
