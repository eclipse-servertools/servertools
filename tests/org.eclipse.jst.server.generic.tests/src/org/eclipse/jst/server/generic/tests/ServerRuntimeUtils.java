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

import org.junit.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.servertype.definition.Classpath;
import org.eclipse.jst.server.generic.servertype.definition.Property;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author <a href="mailto:david.black@capeclear.com">David Black</a>
 */
public final class ServerRuntimeUtils extends Assert {

    private ServerRuntimeUtils() {
        // utility
    }

    public static GenericServer getGenericServer(String serverTypeId, IRuntime runtime) throws Exception {
        GenericServer serverDelegate = null;

        IServerType[] sTypes = ServerCore.getServerTypes();
        for (int i = 0; i < sTypes.length; i++) {
            IServerType serverType = sTypes[i];
            if (serverTypeId.equals(serverType.getId())) {
                IServerWorkingCopy serverWorkingCopy = serverType.createServer(serverTypeId, null, runtime, null);
                assertNotNull("Could not create server", serverWorkingCopy);

                serverDelegate =
                    (GenericServer)serverWorkingCopy.loadAdapter(
                            GenericServer.class,
                            new NullProgressMonitor());
            }
        }

        return serverDelegate;
    }


    public static GenericServerRuntime getGenericServerRuntime(String runtimeTypeId) throws CoreException {
        GenericServerRuntime runtimeDelegate = null;

        IRuntimeType[] runtimesTypes = ServerCore.getRuntimeTypes();
        for (int i = 0; i < runtimesTypes.length; i++) {
            IRuntimeType runtimeType = runtimesTypes[i];
            if (runtimeType.getId().equals(runtimeTypeId)) {
                IRuntime runtimeWorkingCopy = runtimeType.createRuntime(runtimeTypeId, null);
                runtimeDelegate =
                    (GenericServerRuntime)runtimeWorkingCopy.loadAdapter(
                            GenericServerRuntime.class,
                            new NullProgressMonitor());
                break;
            }
        }

        return runtimeDelegate;
    }

    public static void verifyProperty(List property, String id, String value) {
        boolean found = false;
        int count = 0;
        Iterator iter = property.iterator();
        while (iter.hasNext()) {
            Property prop = (Property) iter.next();
            if (prop.getId().equals(id)) {
                assertEquals("Property value does not match", value, prop.getDefault());
                found = true;
                ++count;
            }
        }
        assertTrue("Property not found", found);
        assertEquals("More than one property found", 1, count);
    }

    public static void verifyNoProperty(List property, String id) {
        boolean found = false;
        Iterator iter = property.iterator();
        while (iter.hasNext()) {
            Property prop = (Property) iter.next();
            if (prop.getId().equals(id)) {
                found = true;
                break;
            }
        }
        assertFalse("Property found when there should be none", found);
    }

    public static void verifyClasspath(List classpaths, String id) {
        boolean found = false;
        int count = 0;
        Iterator iter = classpaths.iterator();
        while (iter.hasNext()) {
            Classpath cp = (Classpath) iter.next();
            if (cp.getId().equals(id)) {
                found = true;
                ++count;
            }
        }
        assertTrue("Classpath not found", found);
        assertEquals("More than one classpath found", 1, count);
    }

}
