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
package org.eclipse.wst.server.core.tests.util;

import org.eclipse.wst.server.core.util.NullModuleArtifact;
import junit.framework.TestCase;

public class NullModuleArtifactTestCase extends TestCase {
	protected static NullModuleArtifact nma;

	protected NullModuleArtifact getNullModuleArtifact() {
		if (nma == null) {
			nma = new NullModuleArtifact(null);
		}
		return nma;
	}

	public void testGetModule() {
		assertNull(getNullModuleArtifact().getModule());
	}

	public void testToString() {
		getNullModuleArtifact().toString();
	}
}