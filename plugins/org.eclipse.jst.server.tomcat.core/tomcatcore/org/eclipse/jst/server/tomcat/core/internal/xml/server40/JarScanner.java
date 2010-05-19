/*******************************************************************************
 * Copyright (c) 2010 SAS Institute Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SAS Institute Inc - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.xml.server40;

import org.eclipse.jst.server.tomcat.core.internal.xml.*;
/**
 * 
 */
public class JarScanner extends XMLElement {
	public JarScanner() {
		// do nothing
	}

	public String getClassName() {
		return getAttributeValue("className");
	}

	public String getScanClassPath() {
		return getAttributeValue("scanClassPath");
	}

	public String getScanAllFiles() {
		return getAttributeValue("scanAllFiles");
	}

	public String getScanAllDirectories() {
		return getAttributeValue("scanAllDirectories");
	}

	public void setClassName(String className) {
		setAttributeValue("className", className);
	}

	public void setScanClassPath(String scanClassPath) {
		setAttributeValue("scanClassPath", scanClassPath);
	}

	public void setScanAllFiles(String scanAllFiles) {
		setAttributeValue("scanAllFiles", scanAllFiles);
	}

	public void setScanAllDirectories(String scanAllDirectories) {
		setAttributeValue("scanAllDirectories", scanAllDirectories);
	}
}