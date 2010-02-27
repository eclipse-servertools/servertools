/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests.editor;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.editor.ServerEditorPart;

/* Note: These tests may be executed in any order.  Because null is used as most
 * arguments, the order doesn't currently matter.  If non-null arguments are used,
 * it may be necessary to rewrite the tests to make them truly order independent.
 */

public class ServerEditorPartTestCase extends TestCase {
	protected static ServerEditorPart editor;
	protected static ServerEditorPart initEditor;

	protected ServerEditorPart getServerEditorPart() {
		if (editor == null) {
			editor = new ServerEditorPart() {
				public void createPartControl(Composite parent) {
					// do nothing
				}

				public void setFocus() {
					// do nothing
				}
			};
		}
		return editor;
	}

	protected ServerEditorPart getInitializedServerEditorPart() {
		if (initEditor == null) {
			initEditor = new ServerEditorPart() {
				public void createPartControl(Composite parent) {
					// do nothing
				}

				public void setFocus() {
					// do nothing
				}
			};
			try {
				initEditor.init(null, null);
			}
			catch (Exception e) {
				// ignore
			}
			// Ensure getSections() called
			try {
				initEditor.getErrorMessage();
			} catch (Exception e) {
				// ignore
			}
		}
		return initEditor;
	}

	public void testDoSave() {
		getServerEditorPart().doSave(null);
	}
	
	public void testSaveAs() {
		getServerEditorPart().doSaveAs();
	}
	
	public void testIsDirty() {
		getServerEditorPart().isDirty();
	}
	
	public void testIsSaveAsAllowed() {
		getServerEditorPart().isSaveAsAllowed();
	}
	
	public void testSetErrorMessage() {
		getServerEditorPart().setErrorMessage(null);
	}
	
	public void testUpdateErrorMessage() {
		getServerEditorPart().updateErrorMessage();
	}
	
	public void testGetErrorMessage() {
		try {
			getServerEditorPart().getErrorMessage();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void testGetSaveStatus() {
		getServerEditorPart().getSaveStatus();
	}
	
	public void testGetServer() {
		getInitializedServerEditorPart().getServer();
	}
	
	public void testInsertSections() {
		getInitializedServerEditorPart().insertSections(null, null);
	}

	public void testDispose() {
		getInitializedServerEditorPart().dispose();
		initEditor = null;
	}

	public void testTestProtectedMethods() {
		class MyServerEditorPart extends ServerEditorPart {
			public void testProtected() {
				try {
					getFormToolkit(null);
				} catch (Exception e) {
					// ignore
				}
			}

			public void createPartControl(Composite parent) {
				// do nothing
			}

			public void setFocus() {
				// do nothing
			}
		}
		MyServerEditorPart msep = new MyServerEditorPart();
		msep.testProtected();
	}
}