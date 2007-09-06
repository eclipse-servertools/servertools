/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;
/**
 * 
 */
public class RuntimeFacetMapping {
	private IConfigurationElement element;

	/**
	 * Create a new runtime facet mapping.
	 * 
	 * @param element a configuration element
	 */
	public RuntimeFacetMapping(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * 
	 * @return the id
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/**
	 * 
	 * @return the id
	 */
	public String getRuntimeTypeId() {
		return element.getAttribute("runtimeTypeId");
	}

	/**
	 * 
	 * @return the id
	 */
	public String getVersion() {
		return element.getAttribute("version");
	}

	/**
	 * 
	 * @return the id
	 */
	public String getRuntimeComponent() {
		return element.getAttribute("runtime-component");
	}

	public String toString() {
		return "RuntimeFacetMapping[" + getId() + "]";
	}
}
