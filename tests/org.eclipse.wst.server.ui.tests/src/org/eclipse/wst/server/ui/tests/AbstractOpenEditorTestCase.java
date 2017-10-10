/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests;

import junit.framework.TestCase;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorInput;

public abstract class AbstractOpenEditorTestCase extends TestCase {
	private final String SERVER_EDITOR_ID = "org.eclipse.wst.server.ui.tests.editor";

	public void testOpenEditor() throws Exception {
		IServer server = getServer();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.openEditor(new ServerEditorInput(server.getId()), SERVER_EDITOR_ID, true);
		page.closeEditor(editor, false);
		releaseServer(server);
	}

	public abstract IServer getServer() throws Exception;
	
	public abstract void releaseServer(IServer server) throws Exception;
}