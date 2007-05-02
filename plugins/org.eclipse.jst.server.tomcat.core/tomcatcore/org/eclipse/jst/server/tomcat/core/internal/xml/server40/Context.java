/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.xml.server40;

import org.eclipse.jst.server.tomcat.core.internal.xml.*;
/**
 * 
 */
public class Context extends XMLElement {
	/**
	 * Default constructor
	 */
	public Context() {
		// do nothing
	}

	/**
	 * Get debug attribute.
	 * @return debug attribute value
	 */
	public String getDebug() {
		return getAttributeValue("debug");
	}

	/**
	 * Get docBase attribute.
	 * @return docBase attribute value
	 */
	public String getDocBase() {
		return getAttributeValue("docBase");
	}

	/**
	 * Get path attribute.
	 * @return path attribute value
	 */
	public String getPath() {
		return getAttributeValue("path");
	}

	/**
	 * Get reloadable attribute.
	 * @return reloadable attribute value
	 */
	public String getReloadable() {
		return getAttributeValue("reloadable");
	}

	/**
	 * Get WTP source attribute. Links the context
	 * to a project module.
	 * @return source attribute value
	 */
	public String getSource() {
		return getAttributeValue("source");
	}

	/**
	 * Get context Resources element.  Will create
	 * the element if it does not already exist.
	 * @return resources element.
	 */
	public Resources getResources() {
		return (Resources) findElement("Resources");
	}

	/**
	 * Get context Loader element.  Will create
	 * the element if it does not already exist.
	 * @return loader element.
	 */
	public Loader getLoader() {
		return (Loader) findElement("Loader");
	}
	
	/**
	 * Set debug attribute
	 * @param debug value to set
	 */
	public void setDebug(String debug) {
		setAttributeValue("debug", debug);
	}

	/**
	 * Set docBase attribute.
	 * @param docBase value to set
	 */
	public void setDocBase(String docBase) {
		setAttributeValue("docBase", docBase);
	}

	/**
	 * Set path attribute.
	 * @param path value to set
	 */
	public void setPath(String path) {
		setAttributeValue("path", path);
	}

	/**
	 * Set reloadable attribute.
	 * @param reloadable value to set
	 */
	public void setReloadable(String reloadable) {
		setAttributeValue("reloadable", reloadable);
	}

	/**
	 * Set WTP source attribute. Links the context
	 * to a project module.
	 * @param source value to set
	 */
	public void setSource(String source) {
		setAttributeValue("source", source);
	}
	
	
}
