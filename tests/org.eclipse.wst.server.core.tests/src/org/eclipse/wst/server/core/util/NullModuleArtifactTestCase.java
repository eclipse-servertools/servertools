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
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;

public class NullModuleArtifactTestCase extends TestCase {
	protected static NullModuleArtifact nma;
	
	public static Test suite() {
		return new OrderedTestSuite(NullModuleArtifactTestCase.class, "NullModuleArtifactTestCase");
	}

	public void test00Create() {
		nma = new NullModuleArtifact(null);
	}
	
	public void test01GetModule() {
		assertNull(nma.getModule());
	}
	
	public void test02ToString() {
		nma.toString();
	}
}