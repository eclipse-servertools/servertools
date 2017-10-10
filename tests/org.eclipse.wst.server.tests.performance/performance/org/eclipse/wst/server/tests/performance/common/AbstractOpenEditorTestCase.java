/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.tests.performance.common;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorInput;

public abstract class AbstractOpenEditorTestCase extends ServerPerformanceTestCase {
	public final String SERVER_EDITOR_ID = "org.eclipse.wst.server.ui.editor";

	public void testOpenEditor() throws Exception {
		startMeasuring();
		IServer server = getFirstServer(getServerTypeId());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.openEditor(new ServerEditorInput(server.getId()), SERVER_EDITOR_ID, true);
		
		stopMeasuring();
		commitMeasurements();
		assertPerformance();
		page.closeEditor(editor, false);
	}

	protected abstract String getServerTypeId();
}