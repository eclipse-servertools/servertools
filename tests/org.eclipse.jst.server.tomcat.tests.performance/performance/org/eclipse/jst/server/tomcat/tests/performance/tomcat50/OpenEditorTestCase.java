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
package org.eclipse.jst.server.tomcat.tests.performance.tomcat50;

import org.eclipse.test.performance.Dimension;
import org.eclipse.wst.server.tests.performance.common.AbstractOpenEditorTestCase;

public class OpenEditorTestCase extends AbstractOpenEditorTestCase {
	public void testOpenEditor() throws Exception {
		Dimension[] dims = new Dimension[] { Dimension.ELAPSED_PROCESS, Dimension.USED_JAVA_HEAP };
		tagAsSummary("Open Tomcat editor", dims);
		super.testOpenEditor();
	}

	protected String getServerTypeId() {
		return "org.eclipse.jst.server.tomcat.50";
	}
}
