/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.ui;
/**
 * An generic interface for modifying the VM arguments of a Java
 * server. Can be used to implement the command
 * pattern between the editor and the server.
 */
public interface IVMArgumentEditor {
	/**
	 * Set the VM arguments for this server.
	 *
	 * @param s java.lang.String[]
	 */
	public void setVMArguments(String[] s);
}