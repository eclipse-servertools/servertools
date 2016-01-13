/*******************************************************************************
 * Copyright (c) 2015, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal.model;

import org.eclipse.equinox.p2.metadata.Version;

/**
 * @since 1.1
 */
public class ExtensionProxy implements  IServerExtension{
	private String id;
	private String name;
	private String description;
	private String provider;
	private String uri;
	private String version;
	private String serverId;
	private String vendor;


	public ExtensionProxy(String id, String name, String description, String provider, String uri, String version, String serverId, String vendor) {
		this.id= id;
		this.name= name;
		this.description= description;
		this.provider= provider;
		this.uri = uri;
		this.version = version;
		this.serverId = serverId;
		this.vendor = vendor;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getProvider() {
		return provider;
	}

	public String getId() {
		return id;
	}
	
	public String getURI() {
		return uri;
	}
	
	public Version getVersion() {
		return null;
	}
	
	public String getVersionString() {
		return version;
	}
	
	public String getServerId() {
		return serverId;
	}
	
	public String getRuntimeVendor() {
		return vendor;
	}

}