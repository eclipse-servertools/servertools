/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui;

import org.eclipse.jface.viewers.ILabelProvider;

import org.eclipse.wst.server.ui.internal.ServerLabelProvider;
/**
 * Server UI core.
 * <p>
 * This class provides all its functionality through static members.
 * It is not intended to be subclassed or instantiated.
 * </p>
 * @since 1.0
 */
public final class ServerUICore {
	/**
	 * Cannot instantiate ServerUICore - use static methods.
	 */
	private ServerUICore() {
		// can't create
	}

	/**
	 * Returns a label provider that can be used for all server
	 * objects in the UI.
	 * 
	 * @return a label provider
	 */
	public static ILabelProvider getLabelProvider() {
		return new ServerLabelProvider();
	}
}