/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.server.core.IServer;
/**
 * Simple job scheduling rule that stops a server from starting,
 * publishing, or stopping at the same time.
 * 
 * @deprecated use IServer instance directly
 */
public class ServerSchedulingRule implements ISchedulingRule {
	protected IServer server;

	public ServerSchedulingRule(IServer server) {
		this.server = server;
	}

	public boolean contains(ISchedulingRule rule) {
		return (rule instanceof IServer || rule instanceof ServerSchedulingRule);
	}

	public boolean isConflicting(ISchedulingRule rule) {
		if (!(rule instanceof IServer) && !(rule instanceof ServerSchedulingRule))
			return false;
		
		if (rule instanceof IServer) {
			IServer s = (IServer) rule;
			return server.equals(s);
		}
		
		ServerSchedulingRule ssrule = (ServerSchedulingRule) rule;
		return ssrule.server.equals(server);
	}

	public String toString() {
		return "Server scheduling rule for " + server;
	}
}