/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
  *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.tests;

import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.actions.ServerAction;

public abstract class AbstractServerActionsTestCase extends TestCase {
	public void testServerActions() throws Exception {
		IServer server = getServer();
		List actions = ServerAction.getServerActions();
		for (Iterator it = actions.iterator(); it.hasNext();)
			((ServerAction)it.next()).getDelegate().supports(server);
	}

	protected abstract IServer getServer();
}