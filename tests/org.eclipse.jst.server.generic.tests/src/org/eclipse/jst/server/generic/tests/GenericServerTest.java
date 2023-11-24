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

import junit.framework.TestCase;

import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;

/**
 * @author <a href="mailto:david.black@capeclear.com">David Black</a>
 */
public class GenericServerTest extends TestCase implements TestConstants {

    /**
     * serverTypeId used as a key to the .serverdef file, then
     * the runtimeTypeId used as key to .runtimedef
     *
     * @throws Exception
     */
    public void testServerAndRuntimeInfoForServerDefAndRuntimeDef() throws Exception {

        GenericServerRuntime runtime = ServerRuntimeUtils.getGenericServerRuntime(TEST_RUNTIMETYPE_ID);
        GenericServer server = ServerRuntimeUtils.getGenericServer(TEST_SERVERTYPE_ID, runtime.getRuntime());
        assertNotNull(server);

        ServerRuntime serverRuntime = server.getServerDefinition();

        // Verify Server and Runtime info: properties from runtimedef and serverdef are available
        ServerRuntimeUtils.verifyProperty(serverRuntime.getProperty(), "foo.prop.a", "a runtime property value");
        ServerRuntimeUtils.verifyProperty(serverRuntime.getProperty(), "foo.prop.b", "a server property value");

        ServerRuntimeUtils.verifyClasspath(serverRuntime.getClasspath(), "foo.runtime.classpath");
        ServerRuntimeUtils.verifyClasspath(serverRuntime.getClasspath(), "foo.server.classpath");
    }

    public void testServerAndRuntimeInfoForServerDefOnly() throws Exception {

        GenericServerRuntime runtime = ServerRuntimeUtils.getGenericServerRuntime(TEST_SERVERDEFONLY_RUNTIMETYPE_ID);
        GenericServer server = ServerRuntimeUtils.getGenericServer(TEST_SERVERDEFONLY_SERVERTYPE_ID, runtime.getRuntime());
        assertNotNull(server);

        ServerRuntime serverRuntime = server.getServerDefinition();

        // Verify Server and Runtime info
        ServerRuntimeUtils.verifyProperty(serverRuntime.getProperty(), "foo.prop.a", "a runtime property value");
        ServerRuntimeUtils.verifyProperty(serverRuntime.getProperty(), "foo.prop.b", "a server property value");
        ServerRuntimeUtils.verifyClasspath(serverRuntime.getClasspath(), "foo.classpath");
    }

}
