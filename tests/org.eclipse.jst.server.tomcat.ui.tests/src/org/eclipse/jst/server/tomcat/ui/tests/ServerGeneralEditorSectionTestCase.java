/*******************************************************************************
 * Copyright (c) 2026 Bas Gooren and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Bas Gooren - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.ui.tests;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatVersionHandler;
import org.eclipse.jst.server.tomcat.core.internal.Tomcat110Handler;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.ui.internal.editor.ServerGeneralEditorSection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import junit.framework.TestCase;

public class ServerGeneralEditorSectionTestCase extends TestCase {
	private Shell shell;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		shell = new Shell();
	}

	@Override
	protected void tearDown() throws Exception {
		if (shell != null && !shell.isDisposed())
			shell.dispose();
		super.tearDown();
	}

	public void testTomcat11OptionsInitializedWithoutSecurityManagerControl() {
		TestServerGeneralEditorSection section = new TestServerGeneralEditorSection(shell);
		assertFalse(section.hasSecurityControl());

		section.initialize(new TestTomcat11Server());

		assertTrue(section.isServeModulesWithoutPublishSelected());
		assertTrue(section.isSaveSeparateContextFilesSelected());
	}

	private static class TestServerGeneralEditorSection extends ServerGeneralEditorSection {
		TestServerGeneralEditorSection(Composite parent) {
			noPublish = new Button(parent, SWT.CHECK);
			separateContextFiles = new Button(parent, SWT.CHECK);
			reloadableByDefault = new Button(parent, SWT.CHECK);
			debug = new Button(parent, SWT.CHECK);
		}

		void initialize(TomcatServer server) {
			tomcatServer = server;
			initialize();
		}

		boolean hasSecurityControl() {
			return secure != null;
		}

		boolean isServeModulesWithoutPublishSelected() {
			return noPublish.getSelection();
		}

		boolean isSaveSeparateContextFilesSelected() {
			return separateContextFiles.getSelection();
		}
	}

	private static class TestTomcat11Server extends TomcatServer {
		private final ITomcatVersionHandler versionHandler = new Tomcat110Handler();

		@Override
		public ITomcatVersionHandler getTomcatVersionHandler() {
			return versionHandler;
		}

		@Override
		public boolean isServeModulesWithoutPublish() {
			return true;
		}

		@Override
		public boolean isSaveSeparateContextFiles() {
			return true;
		}

		@Override
		public boolean isModulesReloadableByDefault() {
			return true;
		}

		@Override
		public boolean isSecurityManagerSupported() {
			return false;
		}
	}
}
