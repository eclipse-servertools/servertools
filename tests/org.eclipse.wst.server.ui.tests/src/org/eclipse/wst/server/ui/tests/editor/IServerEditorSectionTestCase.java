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

import org.eclipse.wst.server.ui.editor.IServerEditorSection;
import org.eclipse.wst.server.ui.tests.OrderedTestSuite;
import org.eclipse.wst.server.ui.tests.impl.TestServerEditorSection;
import junit.framework.Test;
import junit.framework.TestCase;

public class IServerEditorSectionTestCase extends TestCase {
	protected static IServerEditorSection section;

	public static Test suite() {
		return new OrderedTestSuite(IServerEditorSectionTestCase.class, "IServerEditorSectionTestCase");
	}

	public void test00Create() {
		section = new TestServerEditorSection();
	}
	
	public void test01Init() {
		section.init(null, null);
	}
	
	public void test02CreateSection() {
		section.createSection(null);
	}
	
	public void test03Dispose() {
		section.dispose();
	}
	
	public void test04GetErrorMessage() {
		section.getErrorMessage();
	}
	
	public void test05GetSaveStatus() {
		section.getSaveStatus();
	}
}