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
package org.eclipse.jst.server.tomcat.tests.performance.tomcat50;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.test.performance.Dimension;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.tests.performance.common.AbstractOpenEditorTestCase;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorInput;

public class OpenEditorAgainTestCase extends AbstractOpenEditorTestCase {
	public static Test suite() {
		return new TestSuite(OpenEditorAgainTestCase.class, "OpenEditorAgainTestCase");
	}

	public void testOpenEditor() throws Exception {
		Dimension[] dims = new Dimension[] { Dimension.ELAPSED_PROCESS, Dimension.USED_JAVA_HEAP };
		tagAsSummary("Open Tomcat editor again", dims);

		for (int i = 0; i < 5; i++) {
			startMeasuring();
			IServer server = getFirstServer(getServerTypeId());
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart editor = page.openEditor(new ServerEditorInput(server.getId()), SERVER_EDITOR_ID, true);
			stopMeasuring();
			page.closeEditor(editor, false);
		}
		
		commitMeasurements();
		assertPerformance();
	}

	protected String getServerTypeId() {
		return "org.eclipse.jst.server.tomcat.50";
	}
}