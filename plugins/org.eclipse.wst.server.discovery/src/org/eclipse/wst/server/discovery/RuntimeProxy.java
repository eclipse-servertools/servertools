/*******************************************************************************
 * Copyright (c) 2015, 2016 IBM Corporation and others.
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
package org.eclipse.wst.server.discovery;

/**
 * @since 1.1
 * 
 */
public class RuntimeProxy {
	private String runtimTypeId;
	private String name;
	private String vendor;
	private String description;
	private String proxyRuntimeId;

	/**
	 * @since 1.2
	 */

	public RuntimeProxy(String id, String name, String description, String vendor) {
		super();
		this.runtimTypeId = id;
		this.name = name;
		this.description = description;
		this.vendor = vendor;
	}

	/**
	 * 
	 * @return the id
	 */
	public String getId() {
		return runtimTypeId;
	}

	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	public String getVendor() {
		return vendor;
	}
	
	public String getVersion() {
		return null;
	}

	/**
	 * @since 1.3 Returns the runtime id for downloadable adapter if specified
	 *        by the site This helps identifying the installed runtimes This is
	 *        important because one downloadable adapter can install more than
	 *        one server/runtime
	 */
	public String getProxyRuntimeId() {
		return proxyRuntimeId;
	}

	/**
	 * @since 1.3
	 */
	public void setProxyRuntimeId(String proxyRuntimeId) {
		this.proxyRuntimeId = proxyRuntimeId;
	}

	public String toString() {
		return "RuntimeType[" + getId() + ", " + getName() + "]";
	}
}