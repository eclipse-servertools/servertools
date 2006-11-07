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

import java.net.URL;
import junit.framework.TestCase;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IURLProvider;

public class IURLProviderTestCase extends TestCase {
	protected static IURLProvider urlProvider;

	public void test00CreateDelegate() throws Exception {
		urlProvider = new IURLProvider() {
			public URL getModuleRootURL(IModule module) {
				return null;
			}
		};
	}
	
	public void test01GetModuleRootURL() throws Exception {
		urlProvider.getModuleRootURL(null);
	}
}