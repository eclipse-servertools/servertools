package org.eclipse.jst.server.ui;
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
/**
 * An generic interface for modifying the path of a Java server. Can be used
 * to implement the command pattern between the editor and the server.
 */
public interface IPathEditor {
	/**
	 * Set the system path for this server.
	 *
	 * @param s java.lang.String
	 */
	public void setPath(String s);

	/**
	 * Set the system path type for this server.
	 *
	 * @param t int
	 */
	public void setPathType(int t);
}