/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.ui.tests;

import org.eclipse.jst.server.tomcat.ui.internal.ConfigurationPropertyTester;
import junit.framework.TestCase;

public class PropertyTestCase extends TestCase {
	public void testProperty() {
		ConfigurationPropertyTester pt = new ConfigurationPropertyTester();
		assertFalse(pt.test(null, "", null, null));
	}
}