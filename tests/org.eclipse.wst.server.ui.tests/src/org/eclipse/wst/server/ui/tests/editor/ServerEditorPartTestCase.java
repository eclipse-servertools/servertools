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
package org.eclipse.wst.server.ui.tests.editor;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.editor.ServerEditorPart;
import org.eclipse.wst.server.ui.tests.OrderedTestSuite;

public class ServerEditorPartTestCase extends TestCase {
	protected static ServerEditorPart editor;

	public static Test suite() {
		return new OrderedTestSuite(ServerEditorPartTestCase.class, "ServerEditorPartTestCase");
	}

	public void test00CreateEditor() {
		editor = new ServerEditorPart() {
			public void createPartControl(Composite parent) {
				// do nothing
			}

			public void setFocus() {
				// do nothing
			}
		};
	}

	public void test02DoSave() {
		editor.doSave(null);
	}
	
	public void test03SaveAs() {
		editor.doSaveAs();
	}
	
	public void test04IsDirty() {
		editor.isDirty();
	}
	
	public void test05IsSaveAsAllowed() {
		editor.isSaveAsAllowed();
	}
	
	public void test06SetErrorMessage() {
		editor.setErrorMessage(null);
	}
	
	public void test07UpdateErrorMessage() {
		editor.updateErrorMessage();
	}
	
	public void test08GetErrorMessage() {
		try {
			editor.getErrorMessage();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test09GetSaveStatus() {
		editor.getSaveStatus();
	}
	
	public void test10Init() {
		try {
			editor.init(null, null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test11GetServer() {
		editor.getServer();
	}
	
	public void test12InsertSections() {
		editor.insertSections(null, null);
	}

	public void test12Dispose() {
		editor.dispose();
	}

	public void test13TestProtectedMethods() {
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