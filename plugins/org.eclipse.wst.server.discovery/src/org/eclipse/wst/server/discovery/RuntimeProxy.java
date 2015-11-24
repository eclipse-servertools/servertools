/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	public String toString() {
		return "RuntimeType[" + getId() + ", " + getName() + "]";
	}
}