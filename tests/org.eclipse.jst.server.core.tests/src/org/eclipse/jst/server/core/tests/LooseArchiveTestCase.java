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
package org.eclipse.jst.server.core.tests;

import org.eclipse.jst.server.core.ILooseArchive;
import org.eclipse.jst.server.core.tests.impl.TestLooseArchive;
import junit.framework.Test;
import junit.framework.TestCase;

public class LooseArchiveTestCase extends TestCase {
	protected static ILooseArchive archive;
	
	public static Test suite() {
		return new OrderedTestSuite(LooseArchiveTestCase.class, "LooseArchiveTestCase");
	}

	public void test00Create() {
		archive = new TestLooseArchive();
	}
	
	public void test01Location() {
		archive.getLocation();
	}
	
	public void test02Binary() {
		archive.isBinary();
	}
}