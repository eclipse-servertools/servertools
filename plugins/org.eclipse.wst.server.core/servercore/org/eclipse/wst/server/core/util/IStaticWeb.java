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
package org.eclipse.wst.server.core.util;
/**
 * A static Web module that could be deployed to Apache or another
 * HTTP server.
 */
public interface IStaticWeb {
	/**
	 * Returns the context root of the Web.
	 * 
	 * @return java.lang.String
	 */
	public String getContextRoot();
}