package org.eclipse.jst.server.tomcat.core;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
/**
 * 
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