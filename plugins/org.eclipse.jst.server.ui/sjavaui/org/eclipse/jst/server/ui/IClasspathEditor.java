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

import org.eclipse.jdt.core.IClasspathEntry;
/**
 * An generic interface for modifying the classpath of a Java server.
 * Can be used to implement the command pattern between the editor and
 * the server.
 */
public interface IClasspathEditor {
	/**
	 * Add classpath entries to the server.
	 *
	 * @param entries org.eclipse.jdt.core.IClasspathEntry[]
	 */
	public void addClasspathEntries(IClasspathEntry[] entries);

	/**
	 * Edit a classpath entry on the server.
	 *
	 * @param entries org.eclipse.jdt.core.IClasspathEntry[]
	 */
	public void editClasspathEntry(int i, IClasspathEntry entry);

	/**
	 * Remove a classpath entry from the server.
	 *
	 * @param entry org.eclipse.jdt.core.IClasspathEntry
	 */
	public void removeClasspathEntry(IClasspathEntry entry);

	/**
	 * Swap two classpath entries in the server.
	 *
	 * @param i int
	 * @param j int
	 */
	public void swapClasspathEntries(int i, int j);
}