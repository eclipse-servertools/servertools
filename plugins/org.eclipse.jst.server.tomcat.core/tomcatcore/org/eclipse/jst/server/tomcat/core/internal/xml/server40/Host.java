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
package org.eclipse.jst.server.tomcat.core.internal.xml.server40;

import org.eclipse.jst.server.tomcat.core.internal.xml.*;
/**
 * 
 */
public class Host extends XMLElement {
	public Host() {
		// do nothing
	}
	
	public String getAppBase() {
		return getAttributeValue("appBase");
	}
	
	public Context getContext(int index) {
		return (Context) findElement("Context", index);
	}
	
	public int getContextCount() {
		return sizeOfElement("Context");
	}
	
	public String getDebug() {
		return getAttributeValue("debug");
	}
	
	public String getName() {
		return getAttributeValue("name");
	}
	
	public void setAppBase(String appBase) {
		setAttributeValue("appBase", appBase);
	}
	
	public void setDebug(String debug) {
		setAttributeValue("debug", debug);
	}
	
	public void setName(String name) {
		setAttributeValue("name", name);
	}
}