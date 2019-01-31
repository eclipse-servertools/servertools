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