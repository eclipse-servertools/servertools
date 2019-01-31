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
 * A Web module.
 */
public class WebModule implements ITomcatWebModule {
	private String docBase;
	private String path;
	private String memento;
	private boolean reloadable;

	/**
	 * WebModule constructor comment.
	 * 
	 * @param path a path
	 * @param docBase a document base
	 * @param memento a memento
	 * @param reloadable <code>true</code> if reloadable
	 */
	public WebModule(String path, String docBase, String memento, boolean reloadable) {
		super();
		this.path = path;
		this.docBase = docBase;
		this.memento = memento;
		this.reloadable = reloadable;
	}

	/**
	 * Get the document base.
	 *
	 * @return java.lang.String
	 */
	public String getDocumentBase() {
		return docBase;
	}

	/**
	 * Return the path. (context root)
	 *
	 * @return java.lang.String
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Return the memento.
	 *
	 * @return java.lang.String
	 */
	public String getMemento() {
		return memento;
	}

	/**
	 * Return true if the web module is auto-reloadable.
	 *
	 * @return java.lang.String
	 */
	public boolean isReloadable() {
		return reloadable;
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof WebModule))
			return false;
		
		WebModule wm = (WebModule) obj;
		if (!getDocumentBase().equals(wm.getDocumentBase()))
			return false;
		if (!getPath().equals(wm.getPath()))
			return false;
		if (!getMemento().equals(wm.getMemento()))
			return false;
		return true;
	}
}