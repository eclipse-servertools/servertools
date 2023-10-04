/***************************************************************************************************
 * Copyright (c) 2005, 2007 Eteration A.S. and Gorkem Ercan.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Gorkem Ercan - initial API and implementation
 **************************************************************************************************/
package org.eclipse.jst.server.generic.tests;

import java.util.HashMap;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntimeTargetHandler;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;

import junit.framework.TestCase;

/**
 *
 *
 * @author Gorkem Ercan
 */
public class GenericServerClasspathRuntimeHandlerTest extends TestCase {

    private static final String SERVER_ROOT = "/dev/java/appservers/JOnAS-4.1.4";
    private static final String SERVER_DEF_NAME = "JOnAS 4.x";
    private IRuntime fRuntime;
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        IRuntimeType type =ServerCore.findRuntimeType("org.eclipse.jst.server.generic.runtime.jonas4");
        IRuntimeWorkingCopy wc = type.createRuntime("testRuntime",null);
        GenericServerRuntime delegate = (GenericServerRuntime)wc.loadAdapter(GenericServerRuntime.class, new NullProgressMonitor());
		HashMap props = new HashMap();
		props.put("mappernames", "");
		props.put("classPathVariableName", "JONAS");
		props.put("serverAddress", "127.0.0.1");
		props.put("jonasBase", SERVER_ROOT);
		props.put("jonasRoot", SERVER_ROOT);
		props.put("protocols", "jrmp");
		props.put("port", "9000");
	    delegate.setServerInstanceProperties(props);
	    delegate.setServerDefinitionId(SERVER_DEF_NAME);
		wc.save(false,null);

		fRuntime = wc.getOriginal();
    }

    /**
     * Constructor for ClasspathRuntimeHandlerTest.
     * @param name
     */
    public GenericServerClasspathRuntimeHandlerTest(String name) {
        super(name);
    }

    public void testResolveClasspathContainer() {
        GenericServerRuntimeTargetHandler handler = new GenericServerRuntimeTargetHandler();
        IClasspathEntry[] entries = handler.resolveClasspathContainer(fRuntime,null);
        assertNotNull("Failed to resolve classpath entries",entries);
        for (int i = 0; i < entries.length; i++) {
            assertTrue("the resolved classpath entry does not start with classpath prefix",(new org.eclipse.core.runtime.Path(SERVER_ROOT)).isPrefixOf(entries[i].getPath()));
        }
    }
}