/*******************************************************************************
 * Copyright (c) 2006 Cape Clear Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Black, Cape Clear Software - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.generic.tests;

import junit.framework.TestCase;

import org.eclipse.jst.server.generic.internal.xml.XMLUtils;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;

/**
 * @author <a href="mailto:david.black@capeclear.com">David Black</a>
 */
public class XMLUtilsTest extends TestCase implements TestConstants {

    private XMLUtils xmlUtils;
    
    
    protected void setUp() throws Exception {
        super.setUp();
        
        xmlUtils = new XMLUtils();
    }
    
    public void testLoadDefinitions() {
        ServerRuntime definition = xmlUtils.getServerTypeDefinition(TestConstants.TEST_SERVERDEFONLY_RUNTIMETYPE_ID);
        assertNotNull(definition);
        
        definition = xmlUtils.getServerTypeDefinition(TestConstants.TEST_SERVERDEFONLY_SERVERTYPE_ID);
        assertNull(definition);

        definition = xmlUtils.getRuntimeTypeDefinition(TestConstants.TEST_SERVERDEFONLY_RUNTIMETYPE_ID);
        assertNull(definition);

        definition = xmlUtils.getServerTypeDefinition(TestConstants.TEST_SERVERTYPE_ID);
        assertNotNull(definition);

        definition = xmlUtils.getRuntimeTypeDefinition(TestConstants.TEST_RUNTIMETYPE_ID);
        assertNotNull(definition);
    }
    
}
