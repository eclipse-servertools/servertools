/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
public class Server extends XMLElement {
	public Server() {
		// do nothing
	}

	public String getDebug() {
		return getAttributeValue("debug");
	}

	public String getName() {
		return getAttributeValue("name");
	}
	
	public String getPort() {
		return getAttributeValue("port");
	}
	
	public Service getService(int index) {
		return (Service) findElement("Service", index);
	}
	
	public int getServiceCount() {
		return sizeOfElement("Service");
	}
	
	public String getShutdown() {
		return getAttributeValue("shutdown");
	}
	
	public void setDebug(String debug) {
		setAttributeValue("debug", debug);
	}
	
	public void setName(String name) {
		setAttributeValue("name", name);
	}
	
	public void setPort(String port) {
		setAttributeValue("port", port);
	}
	
	public void setShutdown(String shutdown) {
		setAttributeValue("shutdown", shutdown);
	}

	public Listener getListener(int index) {
		return (Listener) findElement("Listener", index);
	}
	
	public int getListenerCount() {
		return sizeOfElement("Listener");
	}
}