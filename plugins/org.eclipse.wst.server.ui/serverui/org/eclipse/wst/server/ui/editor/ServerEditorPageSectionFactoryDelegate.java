/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.editor;

import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 *
 */
public abstract class ServerEditorPageSectionFactoryDelegate {
	/**
	 * Returns true if this editor page section should be visible with the given
	 * server and configuration combination. This allows (for
	 * instance) complex configuration pages to only be shown when used
	 * with non-unittest servers.
	 */
	public boolean shouldCreateSection(IServerWorkingCopy server) {
		return true;
	}

	/**
	 * Create the editor page section.
	 */
	public abstract IServerEditorSection createSection();
}