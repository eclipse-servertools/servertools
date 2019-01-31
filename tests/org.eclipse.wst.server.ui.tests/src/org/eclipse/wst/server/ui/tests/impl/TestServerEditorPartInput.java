/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.tests.impl;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;

public class TestServerEditorPartInput implements IServerEditorPartInput{
	public IServerWorkingCopy getServer() {
		return null;
	}

	public boolean isServerReadOnly() {
		return false;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return null;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return null;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
}