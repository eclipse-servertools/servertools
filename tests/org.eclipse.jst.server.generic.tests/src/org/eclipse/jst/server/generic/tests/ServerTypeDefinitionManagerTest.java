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

import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jst.server.generic.core.internal.CorePlugin;
import org.eclipse.jst.server.generic.core.internal.ServerTypeDefinitionManager;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;

/**
 * A server type can define either:
 * 
 * 1. A serverdefinition extension i.e. a .serverdef file, and a runtimedefinition 
 *    i.e. a .runtimedef file. If so, the serverdef is keyed using the serverTypeId,
 *    and the runtimedef is keyed using the runtimeTypeId.
 *    
 * 2. A serverdefinition extension only i.e. a .serverdef file that contains both
 *    the runtime info and the server info. In this case, the serverdef is keyed using
 *    the runtimeTypeId.     
 *
 * @author <a href="mailto:david.black@capeclear.com">David Black</a>
 */
public class ServerTypeDefinitionManagerTest extends TestCase implements TestConstants {

    private ServerTypeDefinitionManager manager;
    
    
    protected void setUp() throws Exception {
        manager = CorePlugin.getDefault().getServerTypeDefinitionManager();
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Use ServerTypeDefinitionManager to load a serverdef 
     * and runtimedef defined in this plugin.
     */
    public void testServerDefAndRuntimeDef() {
        Map properties = Collections.EMPTY_MAP;
        
        /**
         * Implementation of getServerRuntimeDefinition looks up serverdef extensions
         * using the given id. It checks to see if this is a serverTypeId, or a
         * runtimeTypeId. 
         * 
         * If it is a serverTypeId, it loads the serverdef and then proceeds to lookup 
         * runtimedef using the corresponding runtimeTypeId (a server has one runtime, 
         * a runtime may have many servers). It combines the serverdef and the runtimedef 
         * to create the ServerRuntime object. If there is no runtimedef this is an error.
         * 
         * If the id is a runtimeTypeId, then it loads the serverdef and returns the
         * ServerRuntime.  
         */
        ServerRuntime definition = 
            manager.getServerRuntimeDefinition(TEST_SERVERTYPE_ID, TEST_RUNTIMETYPE_ID, properties);
        assertNotNull(definition);
    }

    public void testServerDefOnly() {
        Map properties = Collections.EMPTY_MAP;
        
        ServerRuntime definition = 
            manager.getServerRuntimeDefinition(TEST_SERVERDEFONLY_RUNTIMETYPE_ID, properties);
        assertNotNull(definition);
        
        definition = manager.getServerRuntimeDefinition(TEST_SERVERDEFONLY_SERVERTYPE_ID, properties);
        assertNull(definition);
    }
    
}
