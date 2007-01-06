/**********************************************************************
 * Copyright (c) 2007 SAS Institute, Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    SAS Institute, Inc - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for methods that are used by more that one version
 * of Tomcat.  Use of these methods makes it clear that more than
 * one version will be impacted by changes.
 *
 */
public class TomcatVersionHelper {

	/**
	 * Reads the from the specified InputStream and returns
	 * the result as a String. Each line is terminated by
	 * &quot;\n&quot;.  Returns whatever is read regardless
	 * of any errors that occurs while reading.
	 * 
	 * @param stream InputStream for the contents to be read
	 * @return contents read
	 * @throws IOException if error occurs closing the stream
	 */
	public static String getFileContents(InputStream stream) throws IOException {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(stream));
			String temp = br.readLine();
			while (temp != null) {
				sb.append(temp).append("\n");
				temp = br.readLine();
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load file contents.", e);
		} finally {
			if (br != null)
				br.close();
		}
		return sb.toString();
	}
}
