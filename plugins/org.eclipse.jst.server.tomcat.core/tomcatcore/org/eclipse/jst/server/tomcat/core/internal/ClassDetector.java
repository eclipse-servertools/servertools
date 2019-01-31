/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;
/**
 * Utility class to check for the existence of a class given as an
 * argument. 
 */
public class ClassDetector {
	public static void main(String[] args) {
		if (args == null || args.length != 1) {
			System.out.println("Usage: ClassDetector [className]");
			return;
		}
		
		try {
			Class.forName(args[0]);
			System.out.println("true");
		} catch (Exception e) {
			System.out.println("false");
		}
	}
}