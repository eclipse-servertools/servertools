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

import org.eclipse.wst.server.ui.internal.editor.IOrdered;
import org.eclipse.wst.server.ui.tests.OrderedTestSuite;
import org.eclipse.wst.server.ui.tests.impl.TestOrdered;

import junit.framework.Test;
import junit.framework.TestCase;

public class IOrderedTestCase extends TestCase {
	protected static IOrdered ordered;

	public static Test suite() {
		return new OrderedTestSuite(IOrderedTestCase.class, "IOrderedTestCase");
	}

	public void test00Create() {
		ordered = new TestOrdered();
	}
	
	public void test01GetOrder() {
		ordered.getOrder();
	}
}
