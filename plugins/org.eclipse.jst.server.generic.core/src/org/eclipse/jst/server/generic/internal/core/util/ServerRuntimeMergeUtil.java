/**
 * Copyright (c) 2006, 2024 Cape Clear Software. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.jst.server.generic.internal.core.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jst.server.generic.servertype.definition.Classpath;
import org.eclipse.jst.server.generic.servertype.definition.Property;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;

/**
 * Combines a runtimedef files
 *
 * - properties
 * - classpaths
 *
 * entries with a serverdef, if the serverdef does not already have
 * them - contains check is done by id.
 *
 * @author <a href="mailto:david.black@capeclear.com">David Black</a>
 */
public class ServerRuntimeMergeUtil {

    /**
     * Combines a runtime definition and a server definition
     * into a single logical <code>ServerRuntime</code>.
     *
     * @param serverdef
     * @param runtimedef
     * @return serverdef
     */
    public static ServerRuntime combine(ServerRuntime serverdef, ServerRuntime runtimedef) {
        /**
         * Add properties from runtimedef to serverdef if not already present,
         * this ensures that:
         *
         * (1) while we are affecting the cached copy of serverdef, it is always
         *     required to be combined with its runtimedef (if there is one), and
         *     we check that the property has not already been added
         *
         * (2) serverdef properties can override runtimedef properties
         */
        List<Property> properties = runtimedef.getProperty();
        if (properties != null) {
            Iterator<Property> iter = properties.iterator();
            while (iter.hasNext()) {
                Property prop = iter.next();
                addPropertyIfNotPresent(serverdef.getProperty(), prop);
            }
        }

        /**
         * Add classpaths from runtimedef to serverdef if not already present,
         * this ensures that:
         *
         * (1) while we are affecting the cached copy of serverdef, it is always
         *     required to be combined with its runtimedef (if there is one), and
         *     we check that the classpath has not already been added (by id)
         *
         * (2) serverdef classpath can override runtimedef classpath by id
         */
        List<Classpath> classpaths = runtimedef.getClasspath();
        if (classpaths != null) {
            Iterator<Classpath> iter = classpaths.iterator();
            while (iter.hasNext()) {
                Classpath classpath = iter.next();
                addClasspathIfNotPresent(serverdef.getClasspath(), classpath);
            }
        }

        return serverdef;
    }

    @SuppressWarnings("unchecked")
	private static void addClasspathIfNotPresent(List classpaths, Classpath classpath) {
        if (!containsClasspath(classpaths, classpath.getId())) {
            classpaths.add(classpath);
        }
    }

    private static boolean containsClasspath(List properties, String id) {
        boolean found = false;
        Iterator iter = properties.iterator();
        while (iter.hasNext()) {
            Classpath classpath = (Classpath) iter.next();
            if (classpath.getId().equals(id)) {
                found = true;
                break;
            }
        }
        return found;
    }

    @SuppressWarnings("unchecked")
	private static void addPropertyIfNotPresent(List properties, Property prop) {
        if (!containsProperty(properties, prop.getId())) {
            properties.add(prop);
        }
    }

    private static boolean containsProperty(List properties, String id) {
        boolean found = false;
        Iterator iter = properties.iterator();
        while (iter.hasNext()) {
            Property prop = (Property) iter.next();
            if (prop.getId().equals(id)) {
                found = true;
                break;
            }
        }
        return found;
    }

}
