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
public class Connector extends XMLElement {
	public Connector() {
		// do nothing
	}

	public String getAcceptCount() {
		return getAttributeValue("acceptCount");
	}

	public String getClassName() {
		return getAttributeValue("className");
	}

	public String getDebug() {
		return getAttributeValue("debug");
	}

	public String getMaxProcessors() {
		return getAttributeValue("maxProcessors");
	}

	public String getMinProcessors() {
		return getAttributeValue("minProcessors");
	}

	public String getPort() {
		return getAttributeValue("port");
	}
	
	public String getSecure() {
		return getAttributeValue("secure");
	}
	
	public String getProtocol() {
		return getAttributeValue("protocol");
	}

	public String getProtocolHandlerClassName() {
		return getAttributeValue("protocolHandlerClassName");
	}

	public void setAcceptCount(String acceptCount) {
		setAttributeValue("acceptCount", acceptCount);
	}

	public void setClassName(String className) {
		setAttributeValue("className", className);
	}

	public void setDebug(String debug) {
		setAttributeValue("debug", debug);
	}

	public void setMaxProcessors(String maxProcessors) {
		setAttributeValue("maxProcessors", maxProcessors);
	}

	public void setMinProcessors(String minProcessors) {
		setAttributeValue("minProcessors", minProcessors);
	}

	public void setPort(String port) {
		setAttributeValue("port", port);
	}
	
	public void setProtocolHandlerClassName(String protocolHandlerClassName) {
		setAttributeValue("protocolHandlerClassName", protocolHandlerClassName);
	}
}