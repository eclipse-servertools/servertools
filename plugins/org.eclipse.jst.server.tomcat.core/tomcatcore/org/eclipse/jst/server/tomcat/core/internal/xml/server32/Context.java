/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
package org.eclipse.jst.server.tomcat.core.internal.xml.server32;

import org.eclipse.jst.server.tomcat.core.internal.xml.*;
/**
 * 
 */
public class Context extends XMLElement {
	public Context() {
		// do nothing
	}
	
	public String getCrossContext() {
		return getAttributeValue("crossContext");
	}
	
	public String getDebug() {
		return getAttributeValue("debug");
	}
	
	public String getDocBase() {
		return getAttributeValue("docBase");
	}
	
	public String getPath() {
		return getAttributeValue("path");
	}
	
	public String getReloadable() {
		return getAttributeValue("reloadable");
	}
	
	public String getSource() {
		return getAttributeValue("source");
	}
	
	public String getTrusted() {
		return getAttributeValue("trusted");
	}
	
	public void setCrossContext(String crossContext) {
		setAttributeValue("crossContext", crossContext);
	}
	
	public void setDebug(String debug) {
		setAttributeValue("debug", debug);
	}
	
	public void setDocBase(String docBase) {
		setAttributeValue("docBase", docBase);
	}
	
	public void setPath(String path) {
		setAttributeValue("path", path);
	}
	
	public void setReloadable(String reloadable) {
		setAttributeValue("reloadable", reloadable);
	}
	
	public void setSource(String source) {
		setAttributeValue("source", source);
	}
	
	public void setTrusted(String trusted) {
		setAttributeValue("trusted", trusted);
	}
}