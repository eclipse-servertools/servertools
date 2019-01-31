/**********************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.ui.tests;

import org.eclipse.jst.server.tomcat.core.tests.Tomcat50ServerTestCase;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.tests.AbstractOpenEditorTestCase;

public class OpenEditorTestCase extends AbstractOpenEditorTestCase {
	public IServer getServer() throws Exception {
		return new Tomcat50ServerTestCase().createServer();
	}

	public void releaseServer(IServer server) throws Exception {
		server.delete();
	}
}