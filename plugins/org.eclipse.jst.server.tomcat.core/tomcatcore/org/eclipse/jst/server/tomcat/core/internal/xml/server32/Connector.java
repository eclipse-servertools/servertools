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
public class Connector extends XMLElement {
	public Connector() {
		// do nothing
	}
	
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