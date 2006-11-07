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
package org.eclipse.wst.server.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.model.ClientDelegate;
import org.eclipse.wst.server.core.tests.impl.TestClientDelegate;

public class ClientDelegateTestCase extends TestCase {
	protected static ClientDelegate delegate;

	public void test00CreateDelegate() throws Exception {
		delegate = new TestClientDelegate();
	}
	
	public void test01Supports() throws Exception {
		delegate.supports(null, null, null);
	}
	
	public void test02Launch() throws Exception {
		delegate.launch(null, null, null, null);
	}
}