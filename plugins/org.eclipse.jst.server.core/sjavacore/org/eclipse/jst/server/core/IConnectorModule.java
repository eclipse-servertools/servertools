/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import org.eclipse.core.runtime.IPath;
/**
 * A J2EE connector module.
 */
public interface IConnectorModule extends IJ2EEModule {
	/**
	 * Returns the classpath as an array of absolute IPaths.
	 * 
	 * @param the classpath array
	 */
	public IPath[] getClasspath();
}