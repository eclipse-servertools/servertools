/*******************************************************************************
 * Copyright (c) 2006 Cape Clear Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Black, Cape Clear Software - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.generic.tests;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.jst.server.generic.servertype.definition.Property;

/**
 * @author <a href="mailto:david.black@capeclear.com">David Black</a>
 */
public class GenericServerRuntimeTest extends TestCase implements TestConstants {

    /**
     * runtimeTypeId used as key to .runtimedef
     */
    public void testRuntimeInfoOnly() throws Exception {
        
        GenericServerRuntime runtime = ServerRuntimeUtils.getGenericServerRuntime(TEST_RUNTIMETYPE_ID);
        assertNotNull(runtime);
        
        ServerRuntime runtimeDef = runtime.getServerTypeDefinition();
        assertNotNull(runtimeDef);
        
        // Verify runtime info, and *no* server info
        ServerRuntimeUtils.verifyProperty(runtimeDef.getProperty(), "foo.prop.a", "a runtime property value");
        ServerRuntimeUtils.verifyNoProperty(runtimeDef.getProperty(), "foo.prop.b");
    }

    /**
     * runtimeTypeId used as key to .serverdef
     */
    public void testServerAndRuntimeInfo() throws Exception {
        
        GenericServerRuntime runtime = ServerRuntimeUtils.getGenericServerRuntime(TEST_SERVERDEFONLY_RUNTIMETYPE_ID);
        assertNotNull(runtime);
        
        ServerRuntime serverRuntime = runtime.getServerTypeDefinition();
        assertNotNull(serverRuntime);
        
        // Verify runtime info, and server info
        ServerRuntimeUtils.verifyProperty(serverRuntime.getProperty(), "foo.prop.a", "a runtime property value");
        ServerRuntimeUtils.verifyProperty(serverRuntime.getProperty(), "foo.prop.b", "a server property value");
    }
    
}
