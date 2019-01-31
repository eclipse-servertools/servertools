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
public class Service extends XMLElement {
	public Service() {
		// do nothing
	}

	public Connector getConnector(int index) {
		return (Connector) findElement("Connector", index);
	}

	public int getConnectorCount() {
		return sizeOfElement("Connector");
	}

	public Engine getEngine() {
		return (Engine) findElement("Engine");
	}

	public String getName() {
		return getAttributeValue("name");
	}

	public void setName(String name) {
		setAttributeValue("name", name);
	}
}