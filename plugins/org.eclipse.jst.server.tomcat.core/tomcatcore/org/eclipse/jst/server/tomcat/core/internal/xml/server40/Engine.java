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
public class Engine extends XMLElement {
	public Engine() {
		// do nothing
	}
	
	public String getAppBase() {
		return getAttributeValue("appBase");
	}
	
	public String getClassName() {
		return getAttributeValue("className");
	}
	
	public String getDebug() {
		return getAttributeValue("debug");
	}
	
	public String getDefaultHost() {
		return getAttributeValue("defaultHost");
	}
	
	public Host getHost() {
		return (Host) findElement("Host");
	}
	
	public Host getHost(int index) {
		return (Host) findElement("Host", index);
	}
	
	public int getHostCount() {
		return sizeOfElement("Host");
	}
	
	public String getName() {
		return getAttributeValue("name");
	}
	
	public void setAppBase(String appBase) {
		setAttributeValue("appBase", appBase);
	}
	
	public void setClassName(String className) {
		setAttributeValue("className", className);
	}
	
	public void setDebug(String debug) {
		setAttributeValue("debug", debug);
	}
	
	public void setDefaultHost(String defaultHost) {
		setAttributeValue("defaultHost", defaultHost);
	}
	
	public void setName(String name) {
		setAttributeValue("name", name);
	}
}
