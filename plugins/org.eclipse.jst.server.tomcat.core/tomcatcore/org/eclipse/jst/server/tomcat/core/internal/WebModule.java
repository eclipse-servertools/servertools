/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
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
}