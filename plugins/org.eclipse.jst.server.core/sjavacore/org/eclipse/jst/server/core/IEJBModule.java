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
package org.eclipse.jst.server.core;
/**
 * 
 * @since 1.0
 */
public interface IEJBModule extends IJ2EEModule {
	/**
	 * Returns a version number in the form "x.y.z".
	 *
	 * @return java.lang.String
	 */
	public String getEJBSpecificationVersion();
}