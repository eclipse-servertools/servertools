package org.eclipse.jst.server.tomcat.core.internal.xml.server40;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.jst.server.tomcat.core.internal.xml.*;
/**
 * 
 */
public class Connector extends XMLElement {
	public Connector() { }

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
}