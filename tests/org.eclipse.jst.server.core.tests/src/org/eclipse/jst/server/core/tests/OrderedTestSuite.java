/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.tests;

import java.util.Enumeration;
import java.util.Vector;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class OrderedTestSuite extends TestSuite {
	public OrderedTestSuite(Class theClass) {
		super(theClass);
	}

	public Enumeration tests() {
		Enumeration enum2 = super.tests();
		
		Vector list = new Vector();
		while (enum2.hasMoreElements()) {
			list.add(enum2.nextElement());
		}
		
		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				TestCase t1 = (TestCase) list.get(i);
				TestCase t2 = (TestCase) list.get(j);
				if (t1.getName().compareTo(t2.getName()) > 0) {
					Object obj = list.get(i);
					list.set(i, list.get(j));
					list.set(j, obj);
				}
			}
		}
		
		return list.elements();
	}
}