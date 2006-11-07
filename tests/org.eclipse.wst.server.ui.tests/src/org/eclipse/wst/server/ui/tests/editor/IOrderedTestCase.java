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

import junit.framework.TestCase;

import org.eclipse.wst.server.ui.internal.editor.IOrdered;
import org.eclipse.wst.server.ui.tests.impl.TestOrdered;

public class IOrderedTestCase extends TestCase {
	protected static IOrdered ordered;

	public void test00Create() {
		ordered = new TestOrdered();
	}
	
	public void test01GetOrder() {
		ordered.getOrder();
	}
}
