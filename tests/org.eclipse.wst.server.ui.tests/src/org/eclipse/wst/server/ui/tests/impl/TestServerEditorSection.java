/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.wst.server.ui.editor.IServerEditorSection;

public class TestServerEditorSection implements IServerEditorSection {
	public void init(IEditorSite site, IEditorInput input) {
		// ignore
	}

	public void createSection(Composite parent) {
		// ignore
	}

	public void dispose() {
		// ignore
	}

	public String getErrorMessage() {
		return null;
	}

	public IStatus[] getSaveStatus() {
		return null;
	}
}