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

import org.eclipse.wst.server.ui.editor.ICommandManager;
import org.eclipse.wst.server.ui.tests.OrderedTestSuite;
import org.eclipse.wst.server.ui.tests.impl.TestCommandManager;
import junit.framework.Test;
import junit.framework.TestCase;

public class ICommandManagerTestCase extends TestCase {
	protected static ICommandManager commandManager;

	public static Test suite() {
		return new OrderedTestSuite(ICommandManagerTestCase.class, "ICommandManagerTestCase");
	}

	public void test00Create() {
		commandManager = new TestCommandManager();
	}
	
	public void test01Execute() {
		commandManager.executeCommand(null);
	}
}