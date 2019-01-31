/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
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
package org.eclipse.wst.server.http.core.tests;

import java.util.Enumeration;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class OrderedTestSuite extends TestSuite {
	public OrderedTestSuite(Class theClass) {
		super(theClass);
	}

	@SuppressWarnings("unchecked")
	public Enumeration tests() {
		Enumeration<Test> enum2 = super.tests();
		
		Vector<TestCase> list = new Vector<TestCase>();
		while (enum2.hasMoreElements()) {
			list.add((TestCase) enum2.nextElement());
		}
		
		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				TestCase t1 = list.get(i);
				TestCase t2 = list.get(j);
				if (t1.getName().compareTo(t2.getName()) > 0) {
					TestCase obj = list.get(i);
					list.set(i, list.get(j));
					list.set(j, obj);
				}
			}
		}
		
		return list.elements();
	}
}