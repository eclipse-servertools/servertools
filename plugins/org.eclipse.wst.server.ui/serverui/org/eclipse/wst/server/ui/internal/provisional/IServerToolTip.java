/**********************************************************************
 * Copyright (c) 2007,2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.provisional;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IServer;
/**
 * Provides extra information to the hover over mouse action of a server
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 */
public interface IServerToolTip {
	/**
	 * Allows adopters to add widgets to the tooltip.
	 * 
	 * @param parent the parent
	 * @param server the server
	 */
	public void createContent(Composite parent, IServer server);
}