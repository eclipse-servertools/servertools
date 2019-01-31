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
package org.eclipse.wst.server.ui.internal.editor;

import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.ui.IEditorInput;
/**
 * This is the editor input for the server and server
 * configuration editor. The input includes both a server
 * and server configuration.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerEditorInput extends IEditorInput {
	public static final String EDITOR_ID = ServerUIPlugin.PLUGIN_ID + ".editor";

	/**
	 * Returns the server id.
	 *
	 * @return java.lang.String
	 */
	public String getServerId();
}