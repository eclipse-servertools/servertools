/**********************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Igor Fedorenko & Fabrizio Giustina - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Utility methods for handling loaders in catalina.properties.
 */
public class CatalinaPropertiesUtil {

    /**
     * Don't instantiate
     */
    private CatalinaPropertiesUtil() {
        // unused
    }

    /**
     * Adds a list of path elements to a specific loader in catalina.properties.
     * This method doesn't use java.util.Properties in order to keep file
     * formatting intact.
     * 
     * @param file catalina.properties file
     * @param loader loader name
     * @param elements List of classpath elements
     * @throws IOException
     */
    public static void addGlobalClasspath(File file, String loader,
            String[] elements) throws IOException {

        String propertyName = loader + ".loader";

        BufferedReader br = null;
        StringBuffer buffer = new StringBuffer();
        boolean added = false;

        try {
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {

                if (line.startsWith(propertyName)) {
                    added = true;
                    line = addElements(line, elements);
                }

                buffer.append(line);
                buffer.append("\n");
                line = br.readLine();
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        if (!added) {
            // should never happen with an original catalina.properties,
            // but better handle also files modified by users
            buffer.append(propertyName);
            buffer.append("=");
            for (int i = 0; i < elements.length; i++) {
                buffer.append(elements[i]);
            }
            buffer.append("\n");
        }

        String propertyFile = buffer.toString();

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(propertyFile);
        } finally {
            if (br != null) {
                bw.close();
            }
        }
    }

    /**
     * Append a list of path to the property at the given line and returns the
     * modified name.
     * @param line line (property=value)
     * @param elements classpath elements to add.
     * @return modified line
     */
    private static String addElements(String line, String[] elements) {
        String[] propAndValue = line.split("=");
        String loaderProperty = "";

        if (propAndValue.length > 1) {
            loaderProperty = propAndValue[1];
        }

        Set<String> classpath = new LinkedHashSet<String>();

        StringTokenizer st = new StringTokenizer(loaderProperty, ",");
        while (st.hasMoreTokens()) {
            classpath.add(st.nextToken());
        }

        for (int i = 0; i < elements.length; i++) {
            classpath.add(elements[i]);
        }

        StringBuffer sb = new StringBuffer();
        sb.append(propAndValue[0]);
        sb.append("=");
        for (Iterator it = classpath.iterator(); it.hasNext();) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(',');
            }
        }

        return sb.toString();
    }
}
