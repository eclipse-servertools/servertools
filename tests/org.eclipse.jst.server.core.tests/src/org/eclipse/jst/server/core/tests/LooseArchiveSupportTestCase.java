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

import org.eclipse.jst.server.core.ILooseArchiveSupport;
import org.eclipse.jst.server.core.tests.impl.TestLooseArchiveSupport;
import junit.framework.Test;
import junit.framework.TestCase;

public class LooseArchiveSupportTestCase extends TestCase {
	protected static ILooseArchiveSupport archive;
	
	public static Test suite() {
		return new OrderedTestSuite(LooseArchiveSupportTestCase.class, "LooseArchiveSupportTestCase");
	}

	public void test00Create() {
		archive = new TestLooseArchiveSupport();
	}
	
	public void test01GetLooseArchives() {
		archive.getLooseArchives();
	}
	
	public void test02GetURI() {
		archive.getURI(null);
	}
}