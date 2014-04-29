/**********************************************************************
 * Copyright (c) 2014 SAS Institute and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    SAS Institute - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.xml.server40;

import org.eclipse.jst.server.tomcat.core.internal.xml.XMLElement;

public class PreResources extends XMLElement {

	/**
	 * Get className attribute
	 * @return className attribute value
	 */
	public String getClassName() {
		return getAttributeValue("className");
	}

	/**
	 * Set base attribute.
	 * @param base value to set
	 */
	public void setClassName(String className) {
		setAttributeValue("className", className);
	}

	/**
	 * Get base attribute
	 * @return base attribute value
	 */
	public String getBase() {
		return getAttributeValue("base");
	}

	/**
	 * Set base attribute.
	 * @param base value to set
	 */
	public void setBase(String base) {
		setAttributeValue("base", base);
	}

	/**
	 * Get webAppMount attribute
	 * @return base attribute value
	 */
	public String getWebAppMount() {
		return getAttributeValue("webAppMount");
	}

	/**
	 * Set webAppMount attribute.
	 * @param webAppMount value to set
	 */
	public void setWebAppMount(String webAppMount) {
		setAttributeValue("webAppMount", webAppMount);
	}

	/**
	 * Get internalPath attribute
	 * @return internalPath attribute value
	 */
	public String getInternalPath() {
		return getAttributeValue("internalPath");
	}

	/**
	 * Set internalPath attribute.
	 * @param internalPath value to set
	 */
	public void setInternalPath(String internalPath) {
		setAttributeValue("internalPath", internalPath);
	}

	/**
	 * Get classLoaderOnly attribute
	 * @return classLoaderOnly attribute value
	 */
	public String getClassLoaderOnly() {
		return getAttributeValue("classLoaderOnly");
	}

	/**
	 * Set classLoaderOnly attribute.
	 * @param classLoaderOnly value to set
	 */
	public void setClassLoaderOnly(String classLoaderOnly) {
		setAttributeValue("classLoaderOnly", classLoaderOnly);
	}
}
