/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.server.core.IServer;
/**
 * Simple job scheduling rule that stops a server from starting,
 * publishing, or stopping at the same time.
 */
public class ServerSchedulingRule implements ISchedulingRule {
	protected IServer server;
	
	public ServerSchedulingRule(IServer server) {
		this.server = server;
	}
	
	public boolean contains(ISchedulingRule rule) {
		if (!(rule instanceof ServerSchedulingRule))
			return false;
		
		return true;
	}

	public boolean isConflicting(ISchedulingRule rule) {
		if (!(rule instanceof ServerSchedulingRule))
			return false;
		
		ServerSchedulingRule ssrule = (ServerSchedulingRule) rule;
		return ssrule.server.equals(server);
	}
	
	public String toString() {
		return "Server scheduling rule for " + server;
	}
}