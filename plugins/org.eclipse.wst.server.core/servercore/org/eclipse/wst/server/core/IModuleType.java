/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
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
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IModuleType {
	/**
	 * Returns the type, e.g. "j2ee.ejb".
	 * @return
	 */
	public String getType();

	/**
	 * Returns the version (spec level), e.g. "1.0", "1.3.2"
	 * @return
	 */
	public String getVersion();
}