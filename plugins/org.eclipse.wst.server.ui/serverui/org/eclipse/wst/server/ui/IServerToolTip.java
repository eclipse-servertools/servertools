/**********************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IServer;
/**
 * Provides extra information to the hover over mouse action of a server
 *
 */
public interface IServerToolTip {
	/**
	 * Allows adopters to add widgets to the tooltip. The parent is a StyledText object.
	 * 
	 * @param parent the parent, a StyledText
	 * @param server the server
	 * @return the composite to display
	 */
	public Composite createContent(Composite parent, IServer server);
}