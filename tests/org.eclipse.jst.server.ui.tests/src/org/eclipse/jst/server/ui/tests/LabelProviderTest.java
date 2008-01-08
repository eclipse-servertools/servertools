/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.ui.tests;

import org.eclipse.jst.server.ui.internal.RuntimeLabelProvider;
import org.eclipse.jst.server.ui.internal.StandardJreLabelProvider;

import junit.framework.TestCase;

public class LabelProviderTest extends TestCase {
	public void testLabelProvider() {
		RuntimeLabelProvider lp = new RuntimeLabelProvider(null);
		lp.getLabel();
	}

	public void testJRELabelProvider() {
		StandardJreLabelProvider lp = new StandardJreLabelProvider(null);
		lp.getLabel();
	}
}