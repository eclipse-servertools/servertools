/**********************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Fabrizio Giustina - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.xml.server40;

import org.eclipse.jst.server.tomcat.core.internal.xml.XMLElement;

/**
 * Loader element, optional in Context.
 */
public class Loader extends XMLElement {

	/**
	 * Get className attribute.
     * @return className attribute value
     */
    public String getClassName() {
        return getAttributeValue("className");
    }

    /**
     * Set className attribute.
     * @param className value to set
     */
    public void setClassName(String className) {
        setAttributeValue("className", className);
    }

    /**
     * Get WTP virtualClasspath attribute.
     * @return virtualClasspath value
     */
    public String getVirtualClasspath() {
        return getAttributeValue("virtualClasspath");
    }

    /**
     * Set WTP virtualClasspath attribute.
     * @param virtualClasspath value to set
     */
    public void setVirtualClasspath(String virtualClasspath) {
        setAttributeValue("virtualClasspath", virtualClasspath);
    }

    /**
     * Get useSystemClassLoaderAsParent attribute
     * @return useSystemClassLoaderAsParent value
     */
    public String getUseSystemClassLoaderAsParent() {
        return getAttributeValue("useSystemClassLoaderAsParent");
    }

    /**
     * Set useSystemClassLoaderAsParent attribute.
     * @param useSystemClassLoaderAsParent value to set
     */
    public void setUseSystemClassLoaderAsParent(
            String useSystemClassLoaderAsParent) {
        setAttributeValue("useSystemClassLoaderAsParent",
                useSystemClassLoaderAsParent);
    }

}
