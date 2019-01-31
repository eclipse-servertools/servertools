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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.discovery.RuntimeProxy;
/**
 * @since 1.7
 * 
 */
public class RuntimeTypeProxy implements IRuntimeType {
	private String runtimTypeId;
	private String name;
	private String vendor;
	private String description;
	private String proxyRuntimeId;

	public RuntimeTypeProxy(String id, String name, String description, String vendor, String proxyRuntimeId) {
		super();
		this.runtimTypeId = id;
		this.name = name;
		this.description = description;
		this.vendor = vendor;
		this.proxyRuntimeId = proxyRuntimeId;
	}
	public RuntimeTypeProxy(RuntimeProxy runtimeProxy) {
		super();
		this.runtimTypeId = runtimeProxy.getId();
		this.name = runtimeProxy.getName();
		this.description = runtimeProxy.getDescription();
		this.vendor = runtimeProxy.getVendor();
		this.proxyRuntimeId = runtimeProxy.getProxyRuntimeId();
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
		return "";
	}

	/**
	 * Return the supported module types.
	 * 
	 * @return an array of module types
	 */
	public IModuleType[] getModuleTypes() {
		return null;
	}
	
	public boolean canCreate() {
		return false;
	}

	public IRuntimeWorkingCopy createRuntime(String id, IProgressMonitor monitor) {
		return null;
	}

	public String getProxyRuntimeId() {
		return proxyRuntimeId;
	}
	
	public String toString() {
		return "RuntimeType[" + getId() + ", " + getName() + "]";
	}
}