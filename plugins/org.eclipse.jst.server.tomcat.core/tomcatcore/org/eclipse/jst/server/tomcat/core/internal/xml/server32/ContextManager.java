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
package org.eclipse.jst.server.tomcat.core.internal.xml.server32;

import org.eclipse.jst.server.tomcat.core.internal.xml.*;
/**
 * 
 */
public class ContextManager extends XMLElement {
	public ContextManager() {
		// do nothing
	}
	
	public Connector getConnector(int index) {
		return (Connector) findElement("Connector", index);
	}
	
	public int getConnectorCount() {
		return sizeOfElement("Connector");
	}
	
	public Context getContext(int index) {
		return (Context) findElement("Context", index);
	}
	
	public int getContextCount() {
		return sizeOfElement("Context");
	}
	
	public int getContextInterceptorCount() {
		return sizeOfElement("ContextInterceptor");
	}
	
	public String getDebug() {
		return getAttributeValue("debug");
	}
	
	public String getHome() {
		return getAttributeValue("home");
	}
	
	public int getRequestInterceptorCount() {
		return sizeOfElement("RequestInterceptor");
	}
	
	public String getShowDebugInfo() {
		return getAttributeValue("showDebugInfo");
	}
	
	public String getWorkDir() {
		return getAttributeValue("workDir");
	}
	
	public void setDebug(String debug) {
		setAttributeValue("debug", debug);
	}
	
	public void setHome(String home) {
		setAttributeValue("home", home);
	}
	
	public void setShowDebugInfo(String showDebugInfo) {
		setAttributeValue("showDebugInfo", showDebugInfo);
	}
	
	public void setWorkDir(String workDir) {
		setAttributeValue("workDir", workDir);
	}
}