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
package org.eclipse.jst.server.tomcat.core;
/**
 * 
 */
public interface ITomcatServerWorkingCopy extends ITomcatServer {
	/**
	 * Sets this process to debug mode. This feature only works
	 * with Tomcat v4.0 and above.
	 *
	 * @param b boolean
	 */
	public void setDebug(boolean b);
	
	/**
	 * Sets this process to secure mode.
	 * 
	 * @param b boolean
	 */
	public void setSecure(boolean b);
	
	/**
	 * Sets this server to test environment mode.
	 * 
	 * @param b boolean
	 */
	public void setTestEnvironment(boolean b);
}