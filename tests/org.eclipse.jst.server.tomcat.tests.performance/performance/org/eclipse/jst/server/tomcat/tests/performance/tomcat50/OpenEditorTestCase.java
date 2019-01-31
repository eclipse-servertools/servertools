/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
