/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Version {
	private static final String SEPARATOR = ".";

	private String[] version;

	public Version(String s) {
		StringTokenizer st = new StringTokenizer(s, SEPARATOR);
		List<String> list = new ArrayList<String>();
		
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			int size = str.length();
			for (int i = 0; i < size; i++) {
				if (!Character.isLetterOrDigit(str.charAt(i)))
					throw new NumberFormatException("Version strings cannot contain '" + str.charAt(i) + "'");
			}
			list.add(str);
		}
		
		version = new String[list.size()];
		list.toArray(version);
	}

	public static Version parseVersion(String s) {
		return new Version(s);
	}

	public static int compare(Version v1, Version v2) {
		int v1l = v1.version.length;
		int v2l = v2.version.length;
		
		int size = Math.min(v1l, v2l);
		for (int i = 0; i < size; i++) {
			try {
				double d1 = Double.parseDouble(v1.version[i]);
				double d2 = Double.parseDouble(v2.version[i]);
				
				int c = Double.compare(d1, d2);
				if (c != 0)
					return c;
			} catch (NumberFormatException nfe) {
				// ignore
			}
			
			int c = v1.version[i].compareTo(v2.version[i]);
			if (c != 0)
				return c;
		}
		if (v1l == v2l)
			return 0;
		if (v1l > v2l)
			return 1;
		return -1;
	}
}