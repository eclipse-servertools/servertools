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
import org.eclipse.jst.server.core.internal.SystemProperty;
/**
 * An generic interface for modifying the system properties of
 * a Java server. Can be used to implement the command
 * pattern between the editor and the server.
 */
public interface ISystemPropertyEditor {
	/**
	 * Add a system property to the server.
	 *
	 * @param property org.eclipse.jst.server.ui.SystemProperty
	 */
	public void addSystemProperty(SystemProperty property);

	/**
	 * Edit a system property on the server.
	 *
	 * @param oldProperty org.eclipse.jst.server.ui.SystemProperty
	 * @param newProperty org.eclipse.jst.server.ui.SystemProperty
	 */
	public void modifySystemProperty(SystemProperty oldProperty, SystemProperty newProperty);

	/**
	 * Remove a system property from the server.
	 *
	 * @param property org.eclipse.jst.server.ui.SystemProperty
	 */
	public void removeSystemProperty(SystemProperty property);
}
