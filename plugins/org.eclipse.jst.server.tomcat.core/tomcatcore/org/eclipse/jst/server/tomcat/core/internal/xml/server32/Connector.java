package org.eclipse.jst.server.tomcat.core.internal.xml.server32;
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
	
	public String getClassName() {
		return getAttributeValue("className");
	}
	
	public Parameter getParameter(int index) {
		return (Parameter) findElement("Parameter", index);
	}
	
	public int getParameterCount() {
		return sizeOfElement("Parameter");
	}
	
	public void setClassName(String className) {
		setAttributeValue("className", className);
	}
}