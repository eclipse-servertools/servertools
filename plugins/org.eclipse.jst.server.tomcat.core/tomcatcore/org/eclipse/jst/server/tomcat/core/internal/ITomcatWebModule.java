/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;
/**
 * A Web module deployed on Tomcat.
 */
public interface ITomcatWebModule {
	/**
	 * Get the document base.
	 *
	 * @return java.lang.String
	 */
	public String getDocumentBase();

	/**
	 * Return the path. (context root)
	 *
	 * @return java.lang.String
	 */
	public String getPath();

	/**
	 * Return the memento.
	 *
	 * @return java.lang.String
	 */
	public String getMemento();

	/**
	 * Return true if the web module is auto-reloadable.
	 *
	 * @return java.lang.String
	 */
	public boolean isReloadable();
}