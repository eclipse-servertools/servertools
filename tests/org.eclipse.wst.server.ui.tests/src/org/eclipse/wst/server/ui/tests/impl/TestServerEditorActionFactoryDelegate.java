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

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorSite;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
import org.eclipse.wst.server.ui.internal.provisional.ServerEditorActionFactoryDelegate;

public class TestServerEditorActionFactoryDelegate extends ServerEditorActionFactoryDelegate {

	public IAction createAction(IEditorSite site, IServerEditorPartInput input) {
		return null;
	}
}
